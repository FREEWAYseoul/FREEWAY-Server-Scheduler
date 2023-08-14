package team.free.freewayscheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.free.freewayscheduler.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
