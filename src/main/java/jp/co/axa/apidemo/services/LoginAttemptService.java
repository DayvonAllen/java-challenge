package jp.co.axa.apidemo.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jp.co.axa.apidemo.exceptions.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS= 4;
    private static final int ATTEMPT_INCREMENTER = 1;
    private final LoadingCache<String, Integer> loginAttemptCache;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LoginAttemptService() {
        super();
        this.loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(1, MINUTES).maximumSize(100).build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(String s) throws Exception {
                return 0;
            }
        });
    }

    public void evictUserFromCache(String username){
        loginAttemptCache.invalidate(username);
    }

    public void addUserToCache(String username){
        int attempts = 0;
        try {
            //looks in the cache to see if it can find a value for the username in the cache and adds one to that value if it can
            attempts = ATTEMPT_INCREMENTER + loginAttemptCache.get(username);

            //puts them into the cache
            loginAttemptCache.put(username, attempts);
            logger.info("In add to cache, user: " + username + ". Has attempted to login " + loginAttemptCache.get(username) + " times.");


        } catch (ExecutionException e) {
            throw new AppException("Something went wrong in the cache!");
        }
    }

    public Integer userAttempts(String username){
        try{
            return loginAttemptCache.get(username);
        } catch (ExecutionException e){
            throw new AppException("Something went wrong in user attempts method!");
        }
    }

    public boolean exceededMaxAttempts(String username) {
        try{
            logger.info("Username in exceeded max attempt method: " + username);
            logger.info("Attempts in exceeded max attempt method: " + loginAttemptCache.get(username));
            return loginAttemptCache.get(username) >= MAX_ATTEMPTS;
        } catch (ExecutionException e) {
            throw new AppException("Something went wrong with exceeded max attempt method");
        }
    }
}
