package ce2team1.mentoview.service.dto;

import ce2team1.mentoview.entity.InterviewFeedback;
import lombok.*;

/**
 * DTO for {@link InterviewFeedback}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenerateFeedbackDto {
    private Long questionId;
    private String question;
    private String answer;
}
