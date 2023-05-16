package jp.co.axa.apidemo;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jp.co.axa.apidemo.exceptions.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class ApiDemoApplication {

	@Value("${token.secret}")
	private String secret;

	@Value("${token.issuer}")
	private String issuer;

	@Value("${token.verifier.exception}")
	private String error;

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {
		SpringApplication.run(ApiDemoApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	//verifies jwt token
	@Bean
	public JWTVerifier getVerifier(){
		JWTVerifier verifier;
		try{
			Algorithm algorithm = Algorithm.HMAC512(secret);
			verifier = JWT.require(algorithm).withIssuer(issuer).build();
		} catch (JWTVerificationException e) {
			LOGGER.error(e.getMessage());
			throw new AppException(error);
		}
		return verifier;
	}
}
