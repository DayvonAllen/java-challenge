package jp.co.axa.apidemo.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.axa.apidemo.api.v1.domain.ErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

//handle access denied functionality(when user isn't authorized to do something)
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Value("${message.denied}")
    private String deniedMessage;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {

        ErrorMessage errorMessage = new ErrorMessage(deniedMessage, LocalDateTime.now());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        //getting the output stream from the http response
        OutputStream outputStream = response.getOutputStream();

        //creating an object mapper that will allow for me to map the error object that I created to the http response stream, forces any output bytes to be written out
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, errorMessage);
        //commits response
        outputStream.flush();
    }
}
