package ce2team1.mentoview.entity.atrribute;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum InterviewStatus {
    QUESTION_CREATED, //("질문 생성 완료"),
    USER_COMPLETED, // ("답변 완료"),
    FEEDBACK_CREATED,// ("피드백 생성 완료"),
    COMPLETED; // ("완료");

}
