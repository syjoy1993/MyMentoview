package ce2team1.mentoview.interview.domain.atrribute;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;

@JsonFormat(shape = JsonFormat.Shape.STRING)
@RequiredArgsConstructor
public enum InterviewStatus {

    INTERVIEW_CREATED("인터뷰 생성완료"),// db save Interview

    QUESTION_CREATED("질문 생성 완료"), //AI 질문 생성 + save 완료
    QUESTION_FAILED("질문 생성 실패"), //AI 질문 생성 실패

    USER_COMPLETED("답변 업로드 완료"), // 사용자가 모든 질문에 답변(녹음) 제출
    USER_FAILED("답변 업로드 실패"),
    FEEDBACK_CREATED("피드백 생성 완료"),// AI가 피드백 생성하여 저장함
    FEEDBACK_FAILED("피드백 생성 실패"),// 피드백 생성 실패
    COMPLETED("완료"); // 모두 정상 종료 (최종 상태)

    private final String description;
}
