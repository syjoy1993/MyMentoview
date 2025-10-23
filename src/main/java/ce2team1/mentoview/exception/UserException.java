package ce2team1.mentoview.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class UserException extends RuntimeException {

    private final HttpStatus status;

/*    public UserException() {
        super();
    }*/

    public UserException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public UserException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public UserException(Throwable cause, HttpStatus status) {
        super(cause);
        this.status = status;
    }

    protected UserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }
}
