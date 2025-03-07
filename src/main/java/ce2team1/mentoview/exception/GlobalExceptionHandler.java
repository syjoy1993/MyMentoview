package ce2team1.mentoview.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = { IllegalArgumentException.class })
    public ErrorResult illegalException(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("Bad Request", e.getMessage());
    }

    @ExceptionHandler(value = { UserException.class })
    public ResponseEntity<ErrorResult> userException(UserException e) {
        log.error("[exceptionHandle] ex", e);
        ErrorResult result = new ErrorResult("UserException", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleGlobalException(Exception e) {
        log.error("[exceptionHandle] ex", e);
        String message = (e.getMessage() != null) ? e.getMessage() : "Unexpected error occurred";
        return ResponseEntity.status(400)  // 🚀 500을 400으로 변환
                .body(new ErrorResult("Bad Request", message));
    }
    // validation 전용
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResult> handleServiceException(ServiceException e) {
        HttpStatus status = switch (e.getMessage()) {
            case "User not found" -> HttpStatus.NOT_FOUND;
            case "Password is empty" -> HttpStatus.BAD_REQUEST;
            case "Password does not match" -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status)
                .body(new ErrorResult(status.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler({InterviewException.class})
    public ResponseEntity<ErrorResult> handleInterviewException(InterviewException e) {

        ErrorResult result = new ErrorResult("InterviewException", e.getMessage());

        return ResponseEntity.status(e.getStatus()).body(result);
    }

    @ExceptionHandler({SubscriptionException.class})
    public ResponseEntity<ErrorResult> handleSubscriptionException(SubscriptionException e) {

        ErrorResult result = new ErrorResult("SubscriptionException", e.getMessage());

        return ResponseEntity.status(e.getStatus()).body(result);
    }

    @ExceptionHandler(ResumeException.class)
    public ResponseEntity<ErrorResult> handleResumeException(ResumeException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new ErrorResult("RESUME_ERROR", e.getMessage()));
    }

}
