package ce2team1.mentoview.service;


import ce2team1.mentoview.repository.InterviewQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final InterviewQuestionRepository questionRepository;
}
