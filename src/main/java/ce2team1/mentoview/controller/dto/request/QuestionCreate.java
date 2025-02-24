package ce2team1.mentoview.controller.dto.request;

import ce2team1.mentoview.entity.atrribute.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCreate {
    @NotBlank
    private String question;
    @NotNull
    private Difficulty difficulty;
    @NotNull
    private Long interviewId;
}