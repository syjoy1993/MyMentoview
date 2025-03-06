package ce2team1.mentoview.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InterviewResponseException extends RuntimeException {

    private final HttpStatus status;

    public InterviewResponseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
