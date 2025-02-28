package ce2team1.mentoview.service.dto;


import ce2team1.mentoview.entity.InterviewQuestion;
import ce2team1.mentoview.entity.atrribute.Difficulty;
import lombok.*;

/**
 * DTO for {@link InterviewQuestion}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionDto {
    private Long questionId;
    private String question;
    private Difficulty difficulty;
    private Long interviewId;

    public static QuestionDto of(String question, Difficulty difficulty, Long interviewId) {
        return new QuestionDto(null, question, difficulty, interviewId);
    }
    public static QuestionDto of(Long questionId, String question, Difficulty difficulty, Long interviewId) {
        return new QuestionDto(questionId, question, difficulty, interviewId);
    }

    public static QuestionDto toDto(InterviewQuestion question) {
        return QuestionDto.builder()
                .questionId(question.getQuestionId())
                .question(question.getQuestion())
                .difficulty(question.getDifficulty())
                .interviewId(question.getInterview().getInterviewId())
                .build();

    }

}