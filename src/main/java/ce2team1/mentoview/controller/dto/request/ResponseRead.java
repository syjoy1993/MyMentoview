package ce2team1.mentoview.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ce2team1.mentoview.entity.InterviewResponse}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRead {
    @NotNull
    private Long responseId;
    @NotNull
    private Long questionId;
}