package ce2team1.mentoview.service;

import ce2team1.mentoview.entity.Interview;
import ce2team1.mentoview.entity.atrribute.InterviewStatus;
import ce2team1.mentoview.repository.InterviewFeedbackRepository;
import ce2team1.mentoview.repository.InterviewRepository;
import ce2team1.mentoview.service.dto.GenerateFeedbackDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class FeedbackEventListener {

    private final InterviewRepository interviewRepository;
    private final InterviewFeedbackRepository feedbackRepository;

    // 피드백 개수 카운팅 및 인터뷰 상태 업데이트
    @EventListener
    public void handleFeedbackCompleted(GenerateFeedbackDto generateFeedbackDto) {
        long feedbackCount = feedbackRepository.countByInterviewId(generateFeedbackDto.getInterviewId());
        System.out.println("현재 완료된 피드백 개수 - " + feedbackCount);

        if (feedbackCount == 5) {
            Interview interview = interviewRepository.findById(generateFeedbackDto.getInterviewId())
                    .orElseThrow(() -> new EntityNotFoundException("Interview not found"));

            interview.updateStatus(InterviewStatus.COMPLETED);  // 인터뷰 상태를 "완료"로 변경
        }
    }

}
