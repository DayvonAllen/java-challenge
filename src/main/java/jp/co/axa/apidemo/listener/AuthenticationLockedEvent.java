package jp.co.axa.apidemo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationLockedEvent {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static AuthenticationFailureLockedEvent lockedEvent;

    @EventListener
    public void onAccountLock(AuthenticationFailureLockedEvent event){
        logger.info("User accounted has been locked. In listener...");
        lockedEvent = event;
    }
}
