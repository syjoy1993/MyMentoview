package ce2team1.mentoview.service.dto;

import ce2team1.mentoview.entity.Interview;
import ce2team1.mentoview.entity.InterviewQuestion;
import lombok.*;

/**
 * DTO for {@link InterviewQuestion}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FAQDto {
    private Long questionId;
    private String question;
    private String answer;
    private String feedback;

    public static FAQDto toDto(InterviewQuestion question) {
        return FAQDto.builder()
                .questionId(question.getQuestionId())
                .answer(question.getQuestion())
                .feedback(question.getInterviewFeedback().getFeedback())
                .answer(question.getInterviewResponse().getResponse())
                .build();
    }
}
