package ce2team1.mentoview.interview.application.service;


import ce2team1.mentoview.interview.domain.entity.InterviewQuestion;
import ce2team1.mentoview.interview.domain.entity.InterviewResponse;
import ce2team1.mentoview.interview.presentation.dto.request.ResponseUpdate;
import ce2team1.mentoview.interview.domain.repository.InterviewQuestionRepository;
import ce2team1.mentoview.interview.domain.repository.InterviewResponseRepository;
import ce2team1.mentoview.interview.infra.dto.GenerateFeedbackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResponseService {

    private final InterviewQuestionRepository questionRepository;
    private final InterviewResponseRepository responseRepository;

    @Transactional(readOnly = false)
    public Long createResponse(Long questionId, String fileKey) {
        InterviewQuestion question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Question not found"));
        InterviewResponse response = InterviewResponse.of(fileKey,
                null,
                true,
                Duration.ofSeconds(60),
                question);
        return responseRepository.save(response).getResponseId();
    }

    @Transactional(readOnly = false)
    public GenerateFeedbackDto updateTranscription(ResponseUpdate responseUpdate) {

        InterviewResponse response = responseRepository.findByIdWithQuestionAndInterview(responseUpdate.getResponseId());

        response.updateTranscription(responseUpdate.getResponse());
        InterviewQuestion question = response.getQuestion();
        return GenerateFeedbackDto.builder()
                .interviewId(question.getInterview().getInterviewId())
                .questionId(question.getQuestionId())
                .question(question.getQuestion())
                .answer(responseUpdate.getResponse())
                .build();

    }
}

