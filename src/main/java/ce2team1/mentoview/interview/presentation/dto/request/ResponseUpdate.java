package ce2team1.mentoview.interview.presentation.dto.request;

import ce2team1.mentoview.interview.domain.entity.InterviewResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link InterviewResponse}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUpdate {
    @NotBlank
    private String response;
    @NotBlank
    private Long responseId;
}
