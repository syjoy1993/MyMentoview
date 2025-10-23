package ce2team1.mentoview.service;


import ce2team1.mentoview.controller.dto.request.ResponseUpdate;
import ce2team1.mentoview.entity.InterviewQuestion;
import ce2team1.mentoview.entity.InterviewResponse;
import ce2team1.mentoview.exception.InterviewResponseException;
import ce2team1.mentoview.repository.InterviewQuestionRepository;
import ce2team1.mentoview.repository.InterviewResponseRepository;
import ce2team1.mentoview.service.dto.GenerateFeedbackDto;
import ce2team1.mentoview.service.dto.ResponseTranscribeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ResponseService {

    private final InterviewResponseRepository responseRepository;
    private final InterviewQuestionRepository questionRepository;

    private final FeedbackService feedbackService;
    private final AwsS3Service s3Service;
    private final AwsTranscribeService transcribeService;

    private static final String VOICE_DIR = "voice";

    // InterviewResponse 생성 메서드 (파일 저장 후 Transcribe 호출)
    public void createResponse(Map<Long, MultipartFile> files) {
        List<InterviewQuestion> questionList = questionRepository.findAllByQuestionIdIn(files.keySet());
        List<ResponseTranscribeDto> transcribeList = new ArrayList<>();

        for (InterviewQuestion question : questionList) {
            MultipartFile file = files.get(question.getQuestionId());

            // s3에 file 업로드하고 url 반환
            String fileKey;
            try {
                fileKey = s3Service.uploadS3(file, VOICE_DIR);
            } catch (IOException e) {
                throw new InterviewResponseException("면접 응답 파일 업로드 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            InterviewResponse response = InterviewResponse.of(fileKey,
                    null,
                    true,
                    Duration.ofSeconds(60),
                    question);

            // response 저장
            InterviewResponse savedResponse = responseRepository.save(response);
            ResponseTranscribeDto dto = ResponseTranscribeDto.builder()
                    .responseId(savedResponse.getResponseId())
                    .fileUrl(savedResponse.getS3Key())
                    .build();

            transcribeList.add(dto);
        }

        transcribeService.transcribeInterviewResponse(transcribeList);
    }

    // InterviewResponse 객체 텍스트 저장 후 Feedback 생성 호출
    public void updateTranscriptionAndGenerateFeedback(ResponseUpdate responseUpdate) {

        // 응답 깡통 객체 조회
        InterviewResponse response = responseRepository.findByIdWithQuestionAndInterview(responseUpdate.getResponseId());

        // Transcription 내용 업데이트
        response.updateTranscription(responseUpdate.getResponse());

        // 더티체킹 사용 X
        responseRepository.save(response);
        InterviewQuestion question = response.getQuestion();

        GenerateFeedbackDto dto = GenerateFeedbackDto.builder()
                .interviewId(question.getInterview().getInterviewId())
                .questionId(question.getQuestionId())
                .question(question.getQuestion())
                .answer(responseUpdate.getResponse())
                .build();

        feedbackService.createFeedback(dto);
    }
}
