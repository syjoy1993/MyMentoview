package ce2team1.mentoview.interview.application.service;


import ce2team1.mentoview.exception.InterviewException;
import ce2team1.mentoview.interview.domain.entity.Interview;
import ce2team1.mentoview.interview.domain.entity.InterviewQuestion;
import ce2team1.mentoview.interview.domain.atrribute.Difficulty;
import ce2team1.mentoview.interview.domain.atrribute.InterviewStatus;
import ce2team1.mentoview.interview.domain.repository.InterviewQuestionRepository;
import ce2team1.mentoview.interview.domain.repository.InterviewRepository;
import ce2team1.mentoview.interview.application.dto.QuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionService {
    /*
     * Important
     *  - Database Transaction을 담당한다.
     *  - InterviewEntity 와 연관된 모든 비즈니스로직을 수행
     *  -
     *
     * */


    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    //private final InterviewService interviewService;

    @Transactional(readOnly = false)
    public List<QuestionDto> saveQuestions(long interviewId, List<String> questionsList) {
        Interview interview = interviewRepository.findById(interviewId).orElseThrow(() -> new InterviewException("Interview not found", HttpStatus.NOT_FOUND));

        List<InterviewQuestion> questionEntities = questionsList.stream()
                .filter(question -> question != null && !question.isBlank())
                .map(question -> InterviewQuestion.of(question, Difficulty.EASY, interview))
                .toList();

        if(questionEntities.size() != 5) {
            throw new InterviewException("Question size is not 5, actual: " + questionEntities.size(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<InterviewQuestion> interviewQuestions = questionRepository.saveAll(questionEntities);
        interview.updateStatus(InterviewStatus.QUESTION_CREATED);

        return interviewQuestions.stream()
                .map(QuestionDto::toDto)
                .toList();
    }

    public QuestionDto getQuestionById(Long questionId) {
        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new InterviewException("Question not found", HttpStatus.NOT_FOUND));
        return QuestionDto.toDto(question);
    }
}
