package ce2team1.mentoview.interview.application.orchestrator;

import ce2team1.mentoview.common.infra.storage.AwsS3Service;
import ce2team1.mentoview.exception.InterviewResponseException;
import ce2team1.mentoview.interview.application.dto.QuestionDto;
import ce2team1.mentoview.interview.application.service.InterviewService;
import ce2team1.mentoview.interview.application.service.QuestionService;
import ce2team1.mentoview.interview.application.service.ResponseService;
import ce2team1.mentoview.interview.domain.atrribute.InterviewStatus;
import ce2team1.mentoview.interview.infra.AwsTranscribeService;
import ce2team1.mentoview.interview.infra.dto.GenerateFeedbackDto;
import ce2team1.mentoview.interview.infra.dto.ResponseTranscribeDto;
import ce2team1.mentoview.interview.presentation.dto.request.ResponseUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseOrchestrator {
    // domainService
    private final ResponseService responseService;
    private final InterviewService interviewService;
    private final QuestionService questionService;

    // infraService
    private final AwsS3Service s3Service;
    private final AwsTranscribeService transcribeService;
    private final FeedbackOrchestrator feedbackOrchestrator;

    private static final String VOICE_DIR = "voice";

    // 면접 종료 처리: S3upload -> save database -> Transcribe
    public void processInterviewResponse(Map<Long, MultipartFile> files) {
        List<ResponseTranscribeDto> transcribeDtos = new ArrayList<>();

        // 파일별 처리
        for (Map.Entry<Long, MultipartFile> multipartFileEntry : files.entrySet()) {
            Long questionId = multipartFileEntry.getKey();
            MultipartFile multipartFile = multipartFileEntry.getValue();

            // 보상 트랜잭션
            try {
                // S3upload
                String fileS3Key = s3Service.uploadS3(multipartFile, VOICE_DIR);

                //save database
                Long responseId = responseService.createResponse(questionId, fileS3Key);

                transcribeDtos.add(ResponseTranscribeDto.builder()
                        .responseId(responseId)
                        .fileUrl(fileS3Key)
                        .build()
                );
                // interview.Status -> USER_COMPLETED

            } catch (Exception e) {
                log.error("S3 upload failed for questionId: {}", questionId, e);
                //보상트랜잭션
                handleCompensatingTransaction(questionId);
                // 본래 예외
                throw new InterviewResponseException("면접 응답 파일 업로드 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                        HttpStatus.INTERNAL_SERVER_ERROR);

            }

        }
        if (!transcribeDtos.isEmpty()) {
            transcribeService.transcribeInterviewResponse(transcribeDtos);
        }
        /* Todo: success & fail flag? 부분 성공 / 부분 실패”
        *  - 하나라도 실패면 USER_FAILED or PARTIAL_FAILED
        *  - 전부 성공일 때만 USER_COMPLETED
        * */
        if(!files.isEmpty()) {
            Long anyQuestionId = files.keySet().iterator().next();
            QuestionDto questionDto = questionService.getQuestionById(anyQuestionId);
            interviewService.updateInterviewStatus(questionDto.getInterviewId(), InterviewStatus.USER_COMPLETED);
        }
    }

    // Transcribe Webhook: text update -> requests create FeedBack
    public void handleTranscriptionUpdate(ResponseUpdate responseUpdate) {
        //DB update
        GenerateFeedbackDto feedbackDto = responseService.updateTranscription(responseUpdate);

        // Request create FeedBack to Acync
        feedbackOrchestrator.generateFeedback(feedbackDto);
    }

    //보상트랜잭션
    private void handleCompensatingTransaction(Long questionId) {
        try {
            QuestionDto questionDto = questionService.getQuestionById(questionId);
            interviewService.updateInterviewStatus(questionDto.getInterviewId(), InterviewStatus.USER_FAILED);
        } catch (Exception ex) {
            log.error("Failed to update interview status for questionId: {}", questionId, ex);
        }
    }
}
