package team.free.freewayscheduler.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import team.free.freewayscheduler.annotation.Scheduler;
import team.free.freewayscheduler.service.NotificationService;

@RequiredArgsConstructor
@Scheduler
public class UpdateScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "40 */1 * * * *")
    public void periodicUpdateNotification() {
        notificationService.updateSubwayNotification();
    }
}
