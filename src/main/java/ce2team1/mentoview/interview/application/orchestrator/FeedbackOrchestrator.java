package ce2team1.mentoview.interview.application.orchestrator;

import ce2team1.mentoview.interview.application.service.FeedbackService;
import ce2team1.mentoview.interview.application.service.InterviewService;
import ce2team1.mentoview.interview.infra.AiService;
import ce2team1.mentoview.interview.infra.dto.GenerateFeedbackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackOrchestrator {
    // domain Service
    private final FeedbackService feedbackService;
    private final InterviewService interviewService;

    // infra Service
    private final AiService aiService;

    // Todo: 커스텀 메트릭엔드포인트 입히기
    // metrics
    //private final MeterRegistry meterRegistry;


    // 비동기 피드백 생성 + 후처리 오케스트레이션
    public void generateFeedback(GenerateFeedbackDto request) {
        log.info("Start to Generate Feedback for questionId: {}", request.getQuestionId());

        // call AIServece to generate feedback
        aiService.generateFeedbackFromQA(request)
                // Todo : 매트릭 심기
                .thenAccept(feedbackDto -> {
                    log.info("Feedback Generated for questionId: {}", feedbackDto.getQuestionId());

                    // feedback저장
                    feedbackService.saveFeedback(feedbackDto);

                    // feedback count
                    long count = feedbackService.countFeedback(request.getInterviewId());

                    // Check interview complate?
                    if (count >= 5) {
                        interviewService.completeInterview(request.getInterviewId());
                    }
                })
                .exceptionally(ex -> {
                    log.error("Error Occurred while generating feedback for questionId: {}", request.getQuestionId(), ex);
                            // Todo: 실패시 => 알람? 재시도?
                    return null;
                });

    }
}
