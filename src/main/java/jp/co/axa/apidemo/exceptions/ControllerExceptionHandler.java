package jp.co.axa.apidemo.exceptions;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jp.co.axa.apidemo.api.v1.domain.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${exception.message.account_locked}")
    private String accountLocked;
    @Value("${exception.message.method_is_not_allowed}")
    private String methodIsNotAllowed;
    @Value("${exception.message.internal_server_error_msg}")
    private String internalServerErrorMsg;
    @Value("${exception.message.incorrect_credentials}")
    private String incorrectCredentials;
    @Value("${exception.message.account_disabled}")
    private String accountDisabled;
    @Value("${exception.message.error_processing_file}")
    private String errorProcessingFile;
    @Value("${exception.message.incorrect_permissions}")
    private String incorrectPermissions;
    @Value("${exception.message.no_result}")
    private String noResult;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> exception(Exception e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorMessage> exception(DisabledException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(accountDisabled, LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> exception(BadCredentialsException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(incorrectCredentials, LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> exception(AccessDeniedException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(incorrectPermissions, LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorMessage> exception(LockedException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(accountLocked, LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorMessage> exception(TokenExpiredException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now()), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> exception(HttpRequestMethodNotSupportedException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(methodIsNotAllowed, LocalDateTime.now()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ErrorMessage> exception(NoResultException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(noResult, LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorMessage> exception(IOException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(errorProcessingFile, LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorMessage> exception(AppException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(internalServerErrorMsg, LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ErrorMessage> exception(EmailNotFoundException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorMessage> exception(EmailAlreadyExistException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> exception(UsernameAlreadyExistsException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> exception(UsernameNotFoundException e){
        LOGGER.error(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

}
