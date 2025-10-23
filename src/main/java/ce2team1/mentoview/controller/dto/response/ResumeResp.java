package ce2team1.mentoview.controller.dto.response;


/**
 * DTO for {@link ce2team1.mentoview.entity.Resume}
 */
//@Getter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class ResumeResp {
//    private Long resumeId;
//    private String title;
//    private String fileUrl;
//    private LocalDateTime created_at;
//
//    private Long userId;
//}

import ce2team1.mentoview.entity.Interview;
import ce2team1.mentoview.service.dto.InterviewDto;
import ce2team1.mentoview.service.dto.ResumeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResumeResp {
    private Long resumeId;
    private String title;
    private String s3Key;
    private boolean deleteStatus;
    private LocalDateTime createdAt;
    private List<InterviewResp> interviewList = new ArrayList<>();

    public static ResumeResp from(ResumeDto dto) {
        return new ResumeResp(dto.getResumeId(), dto.getTitle(), dto.getS3Key(), dto.isDeleteStatus(), dto.getCreatedAt(), new ArrayList<>());
    }

    public void updateInterviewList(List<InterviewResp> interviewList) {
        this.interviewList = interviewList;
    }
}