package ce2team1.mentoview.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewDetailResp {
    private Long questionId;
    private String question;
    private String answer;
    private String feedback;
}
