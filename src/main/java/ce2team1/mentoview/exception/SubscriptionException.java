package ce2team1.mentoview.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SubscriptionException extends RuntimeException{
    private final HttpStatus status;

    public SubscriptionException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
