package ce2team1.mentoview.service;


import ce2team1.mentoview.controller.dto.request.InterviewCreate;
import ce2team1.mentoview.entity.Interview;
import ce2team1.mentoview.entity.InterviewQuestion;
import ce2team1.mentoview.entity.Resume;
import ce2team1.mentoview.entity.atrribute.Difficulty;
import ce2team1.mentoview.entity.atrribute.InterviewStatus;
import ce2team1.mentoview.repository.InterviewQuestionRepository;
import ce2team1.mentoview.repository.InterviewRepository;
import ce2team1.mentoview.repository.ResumeRepository;
import ce2team1.mentoview.service.dto.FAQDto;
import ce2team1.mentoview.service.dto.QuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;

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
//    private final PdfService pdfService;
//    private final AiService aiService;

    // 인터뷰 ID로 faq 정보 조회
    @Transactional(readOnly = true)
    public List<FAQDto> getInterviewDetailByInterviewId(Long interviewId) {

        // 인터뷰 가져오기
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        // 인터뷰 상태 완료 확인
        if (interview.getInterviewStatus() != InterviewStatus.COMPLETED) {
            throw new RuntimeException("Interview is not completed");
        }

        // 인터뷰 하위 항목 가져오기
        List<InterviewQuestion> questionList = questionRepository.findQuestionsWithResponsesAndFeedback(interview.getInterviewId());

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
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        return interview.getInterviewStatus().toString();
    }

    // 면접 시작 -> 면접 객체 생성 -> 면접 질문 생성
    public List<QuestionDto> createInterviewQuestion(InterviewCreate interviewCreate) {

        // 이력서 가져오기
        Resume resume = resumeRepository.findById(interviewCreate.getResumeId())
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        // 인터뷰 생성
        Interview interview = Interview.of(InterviewStatus.QUESTION_CREATED,
                interviewCreate.getInterviewType(),
                resume);

        Interview saveInterview = interviewRepository.save(interview);

        // S3에서 이력서 pdf 가져오기
//        ResponseInputStream<?> is = s3Service.getResumeFile(resume.getFileUrl());


        // 가져온 이력서 텍스트 변환 (pdf box)
//        String textFromPDF;
//
//        try {
//            textFromPDF = pdfService.extractTextFromPDF(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to extract text from PDF");
//        }

        // 변환된 텍스트로 질문 생성
//        Map<Integer, String> questionFromResume = aiService.getInterviewQuestionFromResume(textFromPDF);


        // 생성된 질문 5개 저장
//        List<InterviewQuestion> questionList = new ArrayList<>();
//        for (String questionText : questionFromResume.values()) {
//            InterviewQuestion question = InterviewQuestion.of(questionText, Difficulty.EASY, saveInterview);
//            questionList.add(question);
//        }

//        List<InterviewQuestion> saveQuestions = questionRepository.saveAll(questionList);

        // 질문 response 변환 후 반환 (List<QuestionDto>)
//        return saveQuestions.stream().map(sq -> QuestionDto.builder()
//                .questionId(sq.getQuestionId())
//                .question(sq.getQuestion())
//                .interviewId(sq.getInterview().getInterviewId())
//                .build())
//                .toList();
        return new ArrayList<>();
    }

    public void deleteInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        interviewRepository.delete(interview);
    }
}
