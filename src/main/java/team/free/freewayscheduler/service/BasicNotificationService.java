package team.free.freewayscheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.free.freewayscheduler.api.OpenAIRequestManager;
import team.free.freewayscheduler.crawler.NotificationCrawler;
import team.free.freewayscheduler.crawler.NotificationDto;
import team.free.freewayscheduler.domain.Notification;
import team.free.freewayscheduler.repository.NotificationRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

    private static final List<NotificationDto> notificationCache = new ArrayList<>();

    private final NotificationRepository notificationRepository;
    private final NotificationCrawler crawler;
    private final OpenAIRequestManager openAIRequestManager;

    @PostConstruct
    private void init() {
        List<Notification> recentNotifications = notificationRepository.findAll();
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
        List<NotificationDto> notificationCacheSnapshot = List.copyOf(notificationCache);
        List<NotificationDto> notificationDtoList = crawler.crawlingTwitter();
        for (NotificationDto notificationDto : notificationDtoList) {
            if (notificationCache.contains(notificationDto)) {
                continue;
            }

            Notification notification = Notification.from(notificationDto);
            String notificationSummary;
            try {
                notificationSummary = openAIRequestManager.getNotificationSummary(notification);
                log.info("notificationSummary = {}", notificationSummary);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("JSON parsing error");
            }

            notification.updateSummary(notificationSummary);
            try {
                notificationRepository.save(notification);
            } catch (Exception e) {
                e.printStackTrace();
                rollbackCache(notificationCacheSnapshot);
            }

            notificationCache.add(notificationDto);
        }
    }

    private void rollbackCache(List<NotificationDto> notificationCacheSnapshot) {
        notificationCache.clear();
        notificationCache.addAll(notificationCacheSnapshot);
    }
}
