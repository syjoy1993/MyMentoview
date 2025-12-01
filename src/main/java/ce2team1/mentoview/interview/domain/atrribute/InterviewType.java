package ce2team1.mentoview.interview.domain.atrribute;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum InterviewType {
    VOICE,
    VIDEO;
}
