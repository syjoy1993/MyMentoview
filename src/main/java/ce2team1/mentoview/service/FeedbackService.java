package ce2team1.mentoview.service;


import ce2team1.mentoview.repository.InterviewFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final InterviewFeedbackRepository feedbackRepository;
}
