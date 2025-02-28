package ce2team1.mentoview.controller.dto.request;


import ce2team1.mentoview.entity.atrribute.InterviewStatus;
import ce2team1.mentoview.entity.atrribute.InterviewType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ce2team1.mentoview.entity.Interview}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewCreate {
    @NotNull
    private InterviewStatus interviewStatus = InterviewStatus.QUESTION_CREATED;
    @NotNull
    private InterviewType interviewType = InterviewType.VOICE;
    @NotNull
    private Long resumeId;


}