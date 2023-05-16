package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class CustomLogoutHandler implements LogoutHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtTokenProvider jwtTokenProvider;

    public CustomLogoutHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer", "").replace(" ", "");
        logger.info("Security Context before clear: " + SecurityContextHolder.getContext());
        logger.info("In the logout method Token " + token + "\n to the blacklist");
        jwtTokenProvider.addTokenToBlackList(token);
        SecurityContextHolder.clearContext();
        logger.info("Security Context after clear: " + SecurityContextHolder.getContext());
    }
}
