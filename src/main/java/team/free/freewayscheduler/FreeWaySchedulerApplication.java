package team.free.freewayscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import team.free.freewayscheduler.service.NotificationService;

@RestController
@EnableScheduling
@SpringBootApplication
public class FreeWaySchedulerApplication {

    private final NotificationService notificationService;

    public FreeWaySchedulerApplication(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(FreeWaySchedulerApplication.class, args);
    }

    @GetMapping("/test")
    public String test() {
        notificationService.updateSubwayNotification();
        return "test execution";
    }
}
