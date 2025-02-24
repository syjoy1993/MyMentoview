package ce2team1.mentoview.controller.dto.request;


import ce2team1.mentoview.service.dto.FeedbackDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackCreate {
    private String feedback;
    private Integer score;
    @NotNull
    private Long questionId;

    public static FeedbackCreate of(String feedback, int score, Long questionId) {
        return new FeedbackCreate(
                feedback,
                score,
                questionId
        );
    }
    public FeedbackDto toDto() {
        return FeedbackDto.builder()
                .feedback(this.feedback)
                .score(this.score)
                .questionId(this.questionId)
                .build();
    }

}