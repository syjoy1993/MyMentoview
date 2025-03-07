package ce2team1.mentoview.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResumeException extends RuntimeException {

    private final HttpStatus status;

    public ResumeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
