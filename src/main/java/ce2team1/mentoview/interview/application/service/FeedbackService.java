package ce2team1.mentoview.interview.application.service;

import ce2team1.mentoview.interview.domain.entity.InterviewFeedback;
import ce2team1.mentoview.interview.domain.repository.InterviewFeedbackRepository;
import ce2team1.mentoview.interview.application.dto.FeedbackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {
    //
    private final InterviewFeedbackRepository feedbackRepository;


    // 피드백 생성
    @Transactional(readOnly = false)
    public void saveFeedback(FeedbackDto feedbackDto) {
        InterviewFeedback interviewFeedback = InterviewFeedback.of(
                feedbackDto.getFeedback(),
                feedbackDto.getScore(),
                feedbackDto.getQuestionId()
        );
        feedbackRepository.save(interviewFeedback);
    }
    // 피드백 생성 갯수
    @Transactional(readOnly = true)
    public long countFeedback(Long interviewId) {
        return feedbackRepository.countByInterviewId(interviewId);
    }
}
