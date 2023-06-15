package team.free.freewayscheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.free.freewayscheduler.api.OpenAIRequestManager;
import team.free.freewayscheduler.crawler.NotificationDto;
import team.free.freewayscheduler.crawler.SeoulMetroTwitterCrawler;
import team.free.freewayscheduler.domain.Notification;
import team.free.freewayscheduler.repository.NotificationRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

    private static final List<NotificationDto> notificationCache = new ArrayList<>();
    private static final int BEFORE_DATE = 14;

    private final NotificationRepository notificationRepository;
    private final SeoulMetroTwitterCrawler crawler;
    private final OpenAIRequestManager openAIRequestManager;

    @PostConstruct
    private void init() {
        List<Notification> recentNotifications = notificationRepository.findRecentNotifications(LocalDateTime.now().minusDays(14));
        for (Notification recentNotification : recentNotifications) {
            notificationCache.add(NotificationDto.builder()
                    .notificationContent(recentNotification.getContent())
                    .notificationDate(recentNotification.getDateTime())
                    .build());
        }
    }

    @Transactional
    @Override
    public void updateSubwayNotification() {
        List<NotificationDto> notificationDtoList = crawler.crawlingSeoulMetroTwitter();
        for (NotificationDto notificationDto : notificationDtoList) {
            if (existsNotification(notificationDto)) {
                continue;
            }

            Notification notification = Notification.from(notificationDto);
            String notificationSummary;
            try {
                notificationSummary = openAIRequestManager.getNotificationSummary(notification);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("JSON parsing error");
            }

            notification.updateSummary(notificationSummary);
            notificationRepository.save(notification);

            notificationCache.add(NotificationDto.builder()
                    .notificationContent(notification.getContent())
                    .notificationDate(notification.getDateTime())
                    .build());
        }
    }

    private boolean existsNotification(NotificationDto notificationDto) {
        for (NotificationDto recentNotification : notificationCache) {
            if (recentNotification.equals(notificationDto)) {
                return true;
            }
        }
        return false;
    }
}
