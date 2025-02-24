package ce2team1.mentoview.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ce2team1.mentoview.entity.InterviewFeedback}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResp {
    private Long feedbackId;
    private String feedback;
    private Integer score;
    private Long questionId;
}