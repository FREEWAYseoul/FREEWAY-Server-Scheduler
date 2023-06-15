package team.free.freewayscheduler.api.dto.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenAIMessage {

    private String content;
}
