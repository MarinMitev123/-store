package bg.tu.sofia.store.web;

import bg.tu.sofia.store.exception.*;
import java.nio.file.AccessDeniedException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice("bg.tu.sofia.store")
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler({EntityNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorCode> handle(EntityNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorCode(e.getMessage()));
    }

    @ExceptionHandler({InvalidEntityIdException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorCode> handle(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorCode(e.getMessage()));
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorCode> handle(EntityAlreadyExistsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorCode(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorCode> handle(MethodArgumentNotValidException e) {
        Set<String> stringSet =
                e.getBindingResult().getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toSet());
        String join = StringUtils.join(stringSet, ", ");
        log.error(join);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorCode(join));
    }

    @ExceptionHandler(StoreException.class)
    public ResponseEntity<ErrorCode> handle(StoreException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorCode(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorCode> handle(AccessDeniedException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorCode(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorCode> handle(BadCredentialsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorCode(e.getMessage()));
    }
}
