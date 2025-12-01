package ce2team1.mentoview.interview.infra.dto;

import ce2team1.mentoview.interview.domain.entity.InterviewResponse;
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
