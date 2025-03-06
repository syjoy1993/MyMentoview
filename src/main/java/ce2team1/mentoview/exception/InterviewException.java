package ce2team1.mentoview.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InterviewException extends RuntimeException {

    private final HttpStatus status;

    public InterviewException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
