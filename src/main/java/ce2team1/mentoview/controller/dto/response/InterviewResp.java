package ce2team1.mentoview.controller.dto.response;


import ce2team1.mentoview.entity.atrribute.InterviewStatus;
import ce2team1.mentoview.entity.atrribute.InterviewType;
import ce2team1.mentoview.service.dto.InterviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for {@link ce2team1.mentoview.entity.Interview}
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
