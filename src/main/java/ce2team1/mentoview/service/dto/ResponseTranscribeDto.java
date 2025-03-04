package ce2team1.mentoview.service.dto;

import ce2team1.mentoview.entity.InterviewResponse;
import lombok.*;

/**
 * DTO for {@link InterviewResponse}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseTranscribeDto {

    private Long responseId;
    private String fileUrl;
}
