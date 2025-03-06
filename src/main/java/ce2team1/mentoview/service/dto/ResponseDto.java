package ce2team1.mentoview.service.dto;


import ce2team1.mentoview.entity.InterviewResponse;
import lombok.*;

import java.time.Duration;

/**
 * DTO for {@link InterviewResponse}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseDto {
    private Long responseId;
    private String respUrl;
    private String response;
    private Boolean answered;
    private Duration duration;
    private Long questionId;

    private static ResponseDto of(String respUrl, String response, Boolean answered, Duration duration, Long questionId) {
        return new ResponseDto(null, respUrl, response, answered, duration, questionId);
    }
    private static ResponseDto of(Long responseId,String respUrl, String response, Boolean answered, Duration duration, Long questionId) {
        return new ResponseDto(responseId, respUrl, response, answered, duration, questionId);
    }

    private static ResponseDto toDto(InterviewResponse response) {
        return ResponseDto.builder()
                .responseId(response.getResponseId())
                .respUrl(response.getS3Key())
                .response(response.getResponse())
                .answered(response.getAnswered())
                .duration(response.getDuration())
                .questionId(response.getQuestion().getQuestionId())
                .build();

    }
}