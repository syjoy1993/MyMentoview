package ce2team1.mentoview.interview.application.dto;


import ce2team1.mentoview.interview.domain.entity.InterviewFeedback;
import lombok.*;

/**
 * DTO for {@link InterviewFeedback}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedbackDto {
    private Long feedbackId;
    private String feedback;
    private Integer score;
    private Long questionId;

    public static FeedbackDto of(String feedback, Integer score, Long questionId) {
        return new FeedbackDto(null, feedback, score, questionId);
    }
    private static FeedbackDto of(Long feedbackId, String feedback, Integer score, Long questionId) {
        return new FeedbackDto(feedbackId, feedback, score, questionId);
    }

    public static FeedbackDto toDto(InterviewFeedback feedback) {
        return FeedbackDto.builder()
                .feedbackId(feedback.getFeedbackId())
                .feedback(feedback.getFeedback())
                .score(feedback.getScore())
                .questionId(feedback.getQuestion().getQuestionId())
                .build();
    }
}