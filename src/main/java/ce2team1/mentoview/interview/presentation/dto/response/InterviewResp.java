package ce2team1.mentoview.interview.presentation.dto.response;


import ce2team1.mentoview.interview.domain.entity.Interview;
import ce2team1.mentoview.interview.domain.atrribute.InterviewStatus;
import ce2team1.mentoview.interview.domain.atrribute.InterviewType;
import ce2team1.mentoview.interview.application.dto.InterviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for {@link Interview}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewResp {
    private Long interviewId;
    private InterviewStatus interviewStatus;
    private InterviewType interviewType;
    private LocalDateTime created_at;
    private Long resumeId;

    public static InterviewResp from(InterviewDto dto) {
        return InterviewResp.builder()
                .interviewId(dto.getInterviewId())
                .interviewStatus(dto.getInterviewStatus())
                .interviewType(dto.getInterviewType())
                .created_at(dto.getCreatedAt())
                .resumeId(dto.getResumeId())
                .build();
    }
}
