package ce2team1.mentoview.resume.application.dto;


import ce2team1.mentoview.resume.domain.entity.Resume;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for {@link Resume}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResumeDto {
    private Long resumeId;
    private String title;
    private String s3Key;
    private boolean deleteStatus;
    private LocalDateTime createdAt;
    private Long userId;


    private static ResumeDto of(String title, String fileUrl, LocalDateTime createdAt, Long userId) {
        return new ResumeDto(null, title, fileUrl, false, createdAt, userId);
    }
    private static ResumeDto of(Long resumeId,String title, String fileUrl, LocalDateTime createdAt, Long userId) {
        return new ResumeDto(resumeId, title, fileUrl, false, createdAt, userId);
    }
    private static ResumeDto of(Long resumeId,String title, String fileUrl, boolean deleteStatus, LocalDateTime createdAt, Long userId) {
        return new ResumeDto(resumeId, title, fileUrl, deleteStatus, createdAt, userId);
    }
//    private static ResumeDto toDto(Resume resume) {
//        return ResumeDto.builder()
//                .resumeId(resume.getResumeId())
//                .title(resume.getTitle())
//                .fileUrl(resume.getFileUrl())
//                .createdAt(resume.getCreatedAt())
//                .userId(resume.getUser().getUserId())
//                .build();
//    }

    // 얘는 Resume -> ResumeDto 필요해영
    public static ResumeDto from(Resume resume) {
        return new ResumeDto(
                resume.getResumeId(),
                resume.getTitle(),
                resume.getS3Key(),
                resume.isDeleteStatus(),
                resume.getCreatedAt(),
                resume.getUser().getUserId()
        );
    }
}