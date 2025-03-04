package ce2team1.mentoview.service;

import ce2team1.mentoview.entity.InterviewFeedback;
import ce2team1.mentoview.repository.InterviewFeedbackRepository;
import ce2team1.mentoview.service.dto.GenerateFeedbackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final InterviewFeedbackRepository feedbackRepository;
    private final AiService aiService;
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행

    // 피드백 생성 메서드
    @Transactional
    public void createFeedback(GenerateFeedbackDto generateFeedbackDto) {

        // feedback 비동기 생성 요청
        aiService.generateFeedbackFromQA(generateFeedbackDto)
                .thenAccept(feedbackFromQA -> { // 반환 대기

                    // 반환 데이터로 feedback 객체 생성 및 저장
                    InterviewFeedback feedback = InterviewFeedback.of(feedbackFromQA.getFeedback(),
                            10,
                            feedbackFromQA.getQuestionId());

                    feedbackRepository.save(feedback);

                    // 피드백 개수 카운팅 및 인터뷰 상태 업데이트 이벤트 실행
                    eventPublisher.publishEvent(generateFeedbackDto);
                });

    }
}
