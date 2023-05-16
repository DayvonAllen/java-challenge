package jp.co.axa.apidemo.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.axa.apidemo.api.v1.domain.ErrorMessage;
import jp.co.axa.apidemo.listener.AuthenticationLockedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

//whenever the user tries to access the application and has failed to authenticate the Http403ForbiddenEntryPoint will be fired
//this class will extend it so I can override the default functionality
//I have to override the commence method
@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Value("${message.forbidden}")
    private String errMessage;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        ErrorMessage errorMessage;

        if(AuthenticationLockedEvent.lockedEvent != null){
            errorMessage = new ErrorMessage("Your account has been locked", LocalDateTime.now());
            AuthenticationLockedEvent.lockedEvent = null;
        } else {
            errorMessage = new ErrorMessage(errMessage, LocalDateTime.now());
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        //getting the output stream from the http response
        OutputStream outputStream = response.getOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, errorMessage);
        //commits response
        outputStream.flush();
    }
}
