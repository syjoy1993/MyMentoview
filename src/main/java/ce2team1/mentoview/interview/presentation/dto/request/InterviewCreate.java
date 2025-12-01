package ce2team1.mentoview.interview.presentation.dto.request;


import ce2team1.mentoview.interview.domain.entity.Interview;
import ce2team1.mentoview.interview.domain.atrribute.InterviewStatus;
import ce2team1.mentoview.interview.domain.atrribute.InterviewType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link Interview}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewCreate {
    @NotNull
    @Builder.Default
    private InterviewStatus interviewStatus = InterviewStatus.QUESTION_CREATED;
    @NotNull
    @Builder.Default
    private InterviewType interviewType = InterviewType.VOICE;
    @NotNull
    private Long resumeId;


}