package ce2team1.mentoview.interview.domain.atrribute;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Difficulty {
    EASY,
    NORMAL,
    HARD;

}
