package ce2team1.mentoview.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for {@link ce2team1.mentoview.entity.Resume}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumeResp {
    private Long resumeId;
    private String title;
    private String fileUrl;
    private LocalDateTime created_at;

    private Long userId;
}