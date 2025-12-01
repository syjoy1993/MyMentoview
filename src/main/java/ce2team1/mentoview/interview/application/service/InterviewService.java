package ce2team1.mentoview.interview.application.service;


import ce2team1.mentoview.common.infra.storage.AwsS3Service;
import ce2team1.mentoview.exception.InterviewException;
import ce2team1.mentoview.interview.domain.atrribute.InterviewStatus;
import ce2team1.mentoview.interview.domain.entity.Interview;
import ce2team1.mentoview.interview.domain.entity.InterviewQuestion;
import ce2team1.mentoview.interview.domain.repository.InterviewQuestionRepository;
import ce2team1.mentoview.interview.domain.repository.InterviewRepository;
import ce2team1.mentoview.interview.infra.AiService;
import ce2team1.mentoview.interview.infra.dto.FAQDto;
import ce2team1.mentoview.interview.presentation.dto.request.InterviewCreate;
import ce2team1.mentoview.resume.domain.entity.Resume;
import ce2team1.mentoview.resume.domain.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final ResumeRepository resumeRepository;


    private final AiService aiService;
    private final AwsS3Service s3Service;

    private static final String PDF_EXTENSION = ".pdf";


    // 인터뷰 ID로 faq 정보 조회d
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

    @Transactional(readOnly = false)
    public void updateInterviewStatus(Long interviewId, InterviewStatus status) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewException("인터뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        interview.updateStatus(status);
    }

    // 인터뷰 상태값 반환
    public String getInterviewStatus(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new InterviewException("인터뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return interview.getInterviewStatus().toString();
    }

    // 인터뷰 삭제
    @Transactional(readOnly = false)
    public void deleteInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewException("인터뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        interviewRepository.delete(interview);
    }

    @Transactional(readOnly = false)
    public long createInterview(InterviewCreate create, long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new InterviewException("resume not found " + resumeId, HttpStatus.NOT_FOUND));
        Interview interview = Interview.of(
                InterviewStatus.INTERVIEW_CREATED,
                create.getInterviewType(),
                resume
        );
        return interviewRepository.save(interview).getInterviewId();
    }

    @Transactional(readOnly = false)
    public void completeInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InterviewException("인터뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        if(interview.getInterviewStatus() != InterviewStatus.COMPLETED) {
            interview.updateStatus(InterviewStatus.COMPLETED);
        }

    }


}
