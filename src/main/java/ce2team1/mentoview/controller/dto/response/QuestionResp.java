package ce2team1.mentoview.controller.dto.response;

import ce2team1.mentoview.entity.atrribute.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ce2team1.mentoview.entity.InterviewQuestion}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResp {
    private Long questionId;
    private String question;
    private Difficulty difficulty;
    private Long interviewId;
}