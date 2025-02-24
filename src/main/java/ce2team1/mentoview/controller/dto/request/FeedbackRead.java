package ce2team1.mentoview.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRead {
    @NotNull
    private Long feedbackId;
    @NotNull
    private String feedback;
    @NotNull
    private Integer score;
    @NotNull
    private Long questionId;



}