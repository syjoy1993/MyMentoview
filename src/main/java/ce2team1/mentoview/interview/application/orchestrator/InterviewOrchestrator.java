package ce2team1.mentoview.interview.application.orchestrator;

import ce2team1.mentoview.common.infra.storage.AwsS3Service;
import ce2team1.mentoview.interview.application.dto.QuestionDto;
import ce2team1.mentoview.interview.application.service.InterviewService;
import ce2team1.mentoview.interview.infra.AiService;
import ce2team1.mentoview.interview.infra.PdfService;
import ce2team1.mentoview.interview.presentation.dto.request.InterviewCreate;
import ce2team1.mentoview.resume.application.ResumeService;
import ce2team1.mentoview.resume.application.dto.ResumeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterviewOrchestrator {
        /*
         * Important
         *  - saga && 보상 Transaction을 담당한다.
         *  - 인터뷰 생성 프로세스 전체를 관리한다.
         *  - 외부 Service 계층과 맞닿아 있으므로 DataBase트랜젝션에 걸리면 안됨
         *      - DataBase 커넥션을 물고있을경우, 커넥션풀이 말라버림(Repository를 직접 접근XXX)
         * - 자신의 맡은 전체 프로세스가 성공할경우 다음 Orchestrator를 호출
         * - 자신의 맡은 전체 프로세스가 실패할경우 보상 트랜잭션 실행
         * */
    // infraService
    private final PdfService pdfService;
    private final AiService aiService;
    private final AwsS3Service s3Service;
    // domainService
    private final ResumeService resumeService;
    private final InterviewService interviewService;

    //
    private final QuestionOrchestrator questionOrchestrator;


    private static final String PDF_EXTENSION = ".pdf";

    public List<QuestionDto> startInterview(InterviewCreate create) {
        // 이력서 조회
        ResumeDto resume = resumeService.getResumeId(create.getResumeId());
        // 인터뷰 저장
        long interviewId = interviewService.createInterview(create, resume.getResumeId());

        try {
            // S3에서 이력서 PDF 다운로드
            String s3Key = resume.getS3Key();
            // 확장자 검증
            if (!s3Key.toLowerCase().endsWith(PDF_EXTENSION)) {
                throw new IllegalArgumentException("지원서는 PDF 파일만 업로드 가능합니다.");
            }
            String extractedText = pdfService.extractTextFromS3(s3Key).block();

            if (extractedText == null || extractedText.isBlank()) {
                throw new IllegalArgumentException("지원서가 비어있거나 유효하지 않습니다.");
            }

            // 질문 생성 단계 시작 -> QuestionOrchestrator 호출 request를 반환하도록
            return questionOrchestrator.generateQuestions(interviewId, extractedText);
        } catch (Exception e) {
            log.error("인터뷰 생성 프로세스 실패, 보상 트랜잭션 실행 (interviewId={})",interviewId, e);

            // [보상 트랜잭션] 실패 시 생성했던 인터뷰 삭제 (롤백)
            interviewService.deleteInterview(interviewId);
            throw new RuntimeException(e);
        }

    }

}
