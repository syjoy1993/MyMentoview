package ce2team1.mentoview.service.dto;


import ce2team1.mentoview.entity.Resume;
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
    private String fileUrl;
    private LocalDateTime createdAt;
    private Long userId;


    private static ResumeDto of(String title, String fileUrl, LocalDateTime createdAt, Long userId) {
        return new ResumeDto(null, title, fileUrl, null, userId);
    }
    private static ResumeDto of(Long resumeId,String title, String fileUrl, LocalDateTime createdAt, Long userId) {
        return new ResumeDto(resumeId, title, fileUrl, null, userId);
    }

    private static ResumeDto toDto(Resume resume) {
        return ResumeDto.builder()
                .resumeId(resume.getResumeId())
                .title(resume.getTitle())
                .fileUrl(resume.getFileUrl())
                .createdAt(resume.getCreatedAt())
                .userId(resume.getUser().getUserId())
                .build();

    }
}