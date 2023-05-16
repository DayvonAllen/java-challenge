package jp.co.axa.apidemo.listener;

import jp.co.axa.apidemo.entities.UserEntity;
import jp.co.axa.apidemo.repositories.UserRepo;
import jp.co.axa.apidemo.services.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {

    private final LoginAttemptService loginAttemptService;
    private final UserRepo userRepo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService, UserRepo userRepo) {
        this.loginAttemptService = loginAttemptService;
        this.userRepo = userRepo;
    }

    //event fires off whenever someone logs in successfully
    @EventListener
    public void authenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();

        if(principal instanceof User){
            User user = (User) event.getAuthentication().getPrincipal();
            logger.info("In the authentication success method, email: " + user.getUsername());
            UserEntity userEntity = userRepo.findUserEntityByEmail(user.getUsername());
            logger.info("In the authentication success method, username: " + userEntity.getUsername());
            loginAttemptService.evictUserFromCache(userEntity.getUsername());
            logger.info("In the authentication success method, evicted user from the cache.");
        }
    }
}
