package ce2team1.mentoview.interview.presentation.dto.response;

import ce2team1.mentoview.interview.domain.entity.InterviewQuestion;
import ce2team1.mentoview.interview.domain.atrribute.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link InterviewQuestion}
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