package ce2team1.mentoview.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ce2team1.mentoview.entity.Resume}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumeCreate {
    @NotBlank
    private String title;// 유저의 파일명
    @NotBlank
    private String fileUrl; //s3경로
}