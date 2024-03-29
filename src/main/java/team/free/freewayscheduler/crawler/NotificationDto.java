package team.free.freewayscheduler.crawler;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationDto {

    private String notificationContent;
    private LocalDateTime notificationDate;

    @Builder
    public NotificationDto(String notificationContent, LocalDateTime notificationDate) {
        this.notificationContent = notificationContent;
        this.notificationDate = notificationDate;
    }

    @Override
    public boolean equals(Object object) {
        NotificationDto notificationDto = (NotificationDto) object;
        if (!this.notificationContent.equals(notificationDto.getNotificationContent())) {
            return false;
        }
        return this.notificationDate.equals(notificationDto.getNotificationDate());
    }

    @Override
    public int hashCode() {
        int result = notificationContent != null ? notificationContent.hashCode() : 0;
        result = 31 * result + (notificationDate != null ? notificationDate.hashCode() : 0);
        return result;
    }
}
