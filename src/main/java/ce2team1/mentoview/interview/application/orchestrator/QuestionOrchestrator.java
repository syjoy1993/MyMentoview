package ce2team1.mentoview.interview.application.orchestrator;

import ce2team1.mentoview.interview.application.service.InterviewService;
import ce2team1.mentoview.interview.application.service.QuestionService;
import ce2team1.mentoview.interview.infra.AiService;
import ce2team1.mentoview.interview.application.dto.QuestionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionOrchestrator {

    private final AiService aiService;
    private final InterviewService interviewService;
    private final QuestionService questionService;

    // 질문 생성
    public List<QuestionDto> generateQuestions(long interviewId, String extractedText) {
        log.info("Ai Start to Generate Questions for interviewId: {}", interviewId);
        // external I/O
        List<String> questionsList = aiService.getInterviewQuestionFromResume(extractedText);

        return questionService.saveQuestions(interviewId, questionsList);
    }
}
