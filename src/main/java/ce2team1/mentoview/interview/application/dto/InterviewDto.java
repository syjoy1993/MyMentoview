package ce2team1.mentoview.interview.application.dto;


import ce2team1.mentoview.interview.domain.entity.Interview;
import ce2team1.mentoview.interview.domain.atrribute.InterviewStatus;
import ce2team1.mentoview.interview.domain.atrribute.InterviewType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for {@link Interview}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewDto {
    private Long interviewId;
    private InterviewStatus interviewStatus;
    private InterviewType interviewType;
    private LocalDateTime createdAt;
    private Long resumeId;


    public static InterviewDto of(InterviewStatus interviewStatus, InterviewType interviewType, LocalDateTime createdAt, Long resumeId) {
        return new InterviewDto(null, interviewStatus, interviewType, null, resumeId);
    }
    public static InterviewDto of(Long interviewId, InterviewStatus interviewStatus, InterviewType interviewType, LocalDateTime createdAt, Long resumeId) {
        return new InterviewDto(interviewId, interviewStatus, interviewType, null, resumeId);
    }

    public static InterviewDto toDto(Interview interview) {
        return InterviewDto.builder()
                .interviewId(interview.getInterviewId())
                .interviewStatus(interview.getInterviewStatus())
                .interviewType(interview.getInterviewType())
                .createdAt(interview.getCreatedAt())
                .resumeId(interview.getResume().getResumeId())
                .build();

    }

}
