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
public class InterviewRead {
    @NotNull
    private Long interviewId;
    @NotNull
    private Long resumeId;
}
