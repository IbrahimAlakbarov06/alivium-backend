package alivium.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder()
                .uuid(UUID.randomUUID())
                .message(e.getMessage())
                .error("Not Found")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                .uuid(UUID.randomUUID())
                .message(e.getMessage())
                .error("Already Exists")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                .uuid(UUID.randomUUID())
                .message(e.getMessage())
                .error("Business Logic Error")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errors=e.getBindingResult().getFieldErrors().stream()
                .map(err->err.getField()+" :"+err.getDefaultMessage())
                .collect(Collectors.joining(","));

        if (errors.isEmpty()) {
            errors = "Validation Error";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .uuid(UUID.randomUUID())
                        .message(errors)
                        .error("Method ArgumentNot Valid")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                .uuid(UUID.randomUUID())
                .message(e.getMessage())
                .error("Internal Server Error")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

}
