package team.free.freewayscheduler.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import team.free.freewayscheduler.annotation.Scheduler;
import team.free.freewayscheduler.service.NotificationService;

@RequiredArgsConstructor
@Scheduler
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "0 */10 5-23 * * *")
    public void periodicUpdateNotification() {
        notificationService.updateSubwayNotification();
    }
}
