package ce2team1.mentoview.service;


import ce2team1.mentoview.controller.dto.request.InterviewCreate;
import ce2team1.mentoview.entity.Interview;
import ce2team1.mentoview.entity.InterviewQuestion;
import ce2team1.mentoview.entity.Resume;
import ce2team1.mentoview.entity.atrribute.Difficulty;
import ce2team1.mentoview.entity.atrribute.InterviewStatus;
import ce2team1.mentoview.exception.InterviewException;
import ce2team1.mentoview.repository.InterviewQuestionRepository;
import ce2team1.mentoview.repository.InterviewRepository;
import ce2team1.mentoview.repository.ResumeRepository;
import ce2team1.mentoview.service.dto.FAQDto;
import ce2team1.mentoview.service.dto.QuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final ResumeRepository resumeRepository;

    private final PdfService pdfService;
    private final AiService aiService;
    private final AwsS3Service s3Service;

    private static final String PDF_EXTENSION = ".pdf";


    // 인터뷰 ID로 faq 정보 조회
    @Transactional(readOnly = true)
    public List<FAQDto> getInterviewDetailByInterviewId(Long interviewId) {

        // 인터뷰 가져오기
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewException("인터뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 인터뷰 상태 완료 확인
        if (interview.getInterviewStatus() != InterviewStatus.COMPLETED) {
            throw new InterviewException("인터뷰가 완료되지 않았습니다.", HttpStatus.FORBIDDEN);
        }

        // 인터뷰 하위 항목 가져오기
        List<InterviewQuestion> questionList = questionRepository
                .findQuestionsWithResponsesAndFeedback(interview.getInterviewId());

        // 반환할 List<InterviewDetailResp> 생성
        List<FAQDto> faqList = new ArrayList<>();

        // 인터뷰 하위 항목 반환 데이터에 추가
        for (InterviewQuestion question : questionList) {
            FAQDto faq = FAQDto.toDto(question);
            faqList.add(faq);
        }

        return faqList;
    }

    // 인터뷰 상태값 반환
    public String getInterviewStatus(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new InterviewException("인터뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return interview.getInterviewStatus().toString();
    }

    // 면접 시작 -> 면접 객체 생성 -> 면접 질문 생성
    public List<QuestionDto> createInterviewQuestion(InterviewCreate request) {

        // 이력서 조회
        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new InterviewException("해당 id의 이력서가 존재하지 않습니다. - ID: " + request.getResumeId(),
                        HttpStatus.NOT_FOUND));

        // 인터뷰 생성
        Interview savedInterview = createAndSaveInterview(request, resume);

        // 이력서 파일에서 텍스트 추출
        String extractedText = extractResumeTextFromS3(resume.getS3Key());

        // 면접 질문 생성 후 DTO 타입 변환
        return generateAndSaveInterviewQuestions(extractedText, savedInterview);
    }

    // 인터뷰 삭제
    public void deleteInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewException("인터뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        interviewRepository.delete(interview);
    }

    // 인터뷰 생성 및 저장
    private Interview createAndSaveInterview(InterviewCreate request, Resume resume) {
        Interview interview = Interview.of(
                InterviewStatus.QUESTION_CREATED,
                request.getInterviewType(),
                resume
        );

        return interviewRepository.save(interview);
    }

    // S3에서 PDF 텍스트 추출
    private String extractResumeTextFromS3(String fileKey) {

        // 확장자 확인
        if (!fileKey.contains(PDF_EXTENSION)) {
            String[] split = fileKey.split("\\.");
            throw new InterviewException("이력서 파일은 PDF 형식만 가능합니다. 이력서 파일을 다시 업로드해주세요.\n잘못된 파일 확장자 - " + split[split.length - 1],
                    HttpStatus.BAD_REQUEST);
        }

        // pdf 에서 텍스트 추출
        try (ResponseInputStream<GetObjectResponse> s3InputStream = getFileInputStreamFromS3(fileKey)) {
            return pdfService.extractTextFromPDF(s3InputStream);
        } catch (IOException e) {
            throw new InterviewException("이력서 텍스트 추출 중 에러가 발생했습니다. 이력서 파일을 다시 업로드해주세요.",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    // S3 에서 PDF 파일 InputStream 타입으로 가져오기
    private ResponseInputStream<GetObjectResponse> getFileInputStreamFromS3(String fileUrl) {
        return s3Service.getS3ObjectInputStream(fileUrl);
    }

    // 질문 생성 및 저장
    private List<QuestionDto> generateAndSaveInterviewQuestions(String textFromPDF, Interview interview) {

        // AI 질문 생성
        Map<Integer, String> questions = aiService.getInterviewQuestionFromResume(textFromPDF);

        // 질문 객체 리스트 생성
        List<InterviewQuestion> questionEntityList = questions.values().stream()
                .map(questionText -> InterviewQuestion.of(questionText, Difficulty.EASY, interview))
                .toList();

        return questionRepository.saveAll(questionEntityList).stream()
                .map(question -> QuestionDto.builder()
                        .questionId(question.getQuestionId())
                        .question(question.getQuestion())
                        .interviewId(question.getInterview().getInterviewId())
                        .build())
                .toList();
    }
}
