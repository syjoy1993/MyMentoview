package ce2team1.mentoview.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * DTO for {@link ce2team1.mentoview.entity.InterviewResponse}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCreate {
    @NotBlank
    private String respUrl;
    @NotBlank
    private String response;
    @NotNull
    private Boolean answered = false;
    @NotNull
    private Duration duration = Duration.ZERO;

    private Long questionId;
}