package jp.co.axa.apidemo.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.axa.apidemo.api.v1.domain.LoginDto;
import jp.co.axa.apidemo.api.v1.domain.UserDto;
import jp.co.axa.apidemo.exceptions.AppException;
import jp.co.axa.apidemo.security.JwtTokenProvider;
import jp.co.axa.apidemo.services.LoginAttemptService;
import jp.co.axa.apidemo.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationFilter(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.loginAttemptService = loginAttemptService;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDto loginDto = new ObjectMapper().readValue(request.getInputStream(), LoginDto.class);

            logger.info("Security context in attempt auth method is now: " + SecurityContextHolder.getContext());

            Authentication auth = getAuthenticationManager()
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            logger.info("Authentication: " + auth);

            return auth;
        } catch (IOException e){
            logger.error("Exception occurred while mapping request to login object. Message: " + e.getMessage());
            throw new AppException("Authentication occurred, couldn't process credentials!");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();


        System.out.println(username);
        UserDto userEntity = userService.findUserByEmail(username);
        String userId = userEntity.getUserId().toString();
        String remoteAddr = getClientIp(request);
        String ua = getUserAgent(request);


        String token = jwtTokenProvider.generateJwtToken(((User) authResult.getPrincipal()), remoteAddr, ua);
        response.addHeader("token", token);
        response.addHeader("userId", userId);
    }

    public static String getClientIp(HttpServletRequest request){
        String remoteAddr = "";
        if(request != null){
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if(remoteAddr == null || remoteAddr.equals("")){
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

    public static String getUserAgent(HttpServletRequest request){
        String ua = "";
        if(request != null){
            ua = request.getHeader("User-Agent");
        }
        return ua;
    }

}