package ce2team1.mentoview.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * DTO for {@link ce2team1.mentoview.entity.InterviewResponse}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResp {
    private Long responseId;
    private String respUrl;
    private String response;
    private Boolean answered;
    private Duration duration;
    private Long questionId;
}