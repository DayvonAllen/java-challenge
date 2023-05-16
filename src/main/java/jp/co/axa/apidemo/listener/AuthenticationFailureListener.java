package jp.co.axa.apidemo.listener;

import jp.co.axa.apidemo.services.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener {

    private final LoginAttemptService loginAttemptService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    //event fires off whenever someone puts in the wrong credentials
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event){
        Object principal = event.getAuthentication().getPrincipal();

        if(principal instanceof String){
            String username = (String) event.getAuthentication().getPrincipal();
            logger.info("In the authentication failure listener, username: " + username);
            loginAttemptService.addUserToCache(username);
            logger.info("User added to cache.");
            logger.info("User attempts: " + loginAttemptService.userAttempts(username));

        }
    }
}
