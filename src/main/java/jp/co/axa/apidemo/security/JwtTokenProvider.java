package jp.co.axa.apidemo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jp.co.axa.apidemo.entities.Token;
import jp.co.axa.apidemo.filter.AuthenticationFilter;
import jp.co.axa.apidemo.repositories.TokenRepo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${token.secret}")
    private String secret;
    private final Environment environment;
    private final JWTVerifier jwtVerifier;
    private final TokenRepo tokenRepo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtTokenProvider(Environment environment, JWTVerifier jwtVerifier, TokenRepo tokenRepo) {
        this.environment = environment;
        this.jwtVerifier = jwtVerifier;
        this.tokenRepo = tokenRepo;
    }

    //takes a user principal because I need to verify that a user exist in our system before I generate a token
    //a claim is a user authority or permissions, usually an array
    //subject is going to be the user id or username(the subject is the owner of the token)
    public String generateJwtToken(UserDetails userDetails, String remoteAddr, String ua){
        List<String> claims = getClaimsFromUser(userDetails);
        //once here the user has passed authentication and is a known user.

        logger.info("In the generate token method User's IP address: " + remoteAddr);
        logger.info("In the generate token method User's User Agent: " + ua);
        Date expirationDate = new Date(System.currentTimeMillis() + 432_000_000L);
        return JWT
                .create()
                .withIssuer(environment.getProperty("token.provider"))
                .withAudience(environment.getProperty("token.audience"))
                .withIssuedAt(new Date())
                .withSubject(userDetails.getUsername())
                .withClaim(environment.getProperty("token.authorities"),claims)
                .withClaim("ip", remoteAddr)
                .withClaim("ua", ua)
                //add date in case token goes into blacklist, remove all expired tokens from blacklist
                .withClaim("expiration", expirationDate.toString())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    //get authorities from a token, when the user is trying to access restricted content I need to check the authorities
    // on the token. Simple granted authority extends granted authority
    public List<GrantedAuthority> getAuthorities(String token){
        List<String> claims = getAuthorityClaimsFromToken(token);
        if(claims == null){
            return null;
        }
        //collect authority claims from token and map them into simple granted authorities so spring security can verify them
        return claims.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    //tells spring that this user is authenticated, process the request(after the token is verified)
    //sets authentication in spring security context for a user
    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public Boolean isTokenValid(String username, String token){
        return StringUtils.isNotEmpty(username) && !isTokenExpired(jwtVerifier, token);
    }

    public String getSubject(String token) {
        //getting subject from verified token
        System.out.println(token);
        return jwtVerifier.verify(token).getSubject();
    }


    private Boolean isTokenExpired(JWTVerifier jwtVerifier, String token){
        //gets the valid tokens expiration date
        Date expiration = jwtVerifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private List<String> getClaimsFromUser(UserDetails userPrincipal){
        if(userPrincipal.getAuthorities() == null){
            return null;
        }
        List<String> authorities = new ArrayList<>();
        userPrincipal.getAuthorities().forEach((authority) -> authorities.add(authority.getAuthority()));
        return authorities;
    }

    private List<String> getAuthorityClaimsFromToken(String token){
        //verifies token and then grab all of the claims off it and returns an array list of claims
        return jwtVerifier.verify(token).getClaim(environment.getProperty("token.authorities")).asList(String.class);
    }

    private String getIpClaimFromToken(String token){
        //verifies token and then grab all off the claims of it and returns an array list of claims
        return jwtVerifier.verify(token).getClaim("ip").asString();
    }

    private String getUaClaimFromToken(String token){
        return jwtVerifier.verify(token).getClaim("ua").asString();
    }

    private String getExpirationDateFromToken(String token){
        String newToken = jwtVerifier.verify(token).getClaim("expiration").asString();
        logger.info("Token exp date: " + newToken);
        return  newToken;
    }

    public Boolean addTokenToBlackList(String receivedToken){
        Token token = new Token(jwtVerifier.verify(receivedToken).getToken(), getExpirationDateFromToken(receivedToken));
        logger.info("Saving token: " + receivedToken + "\nTo the blacklist");
        tokenRepo.save(token);
        return true;
    }

    public Boolean verifiedIp(String token, HttpServletRequest request){
        return AuthenticationFilter.getClientIp(request).equals(getIpClaimFromToken(token));
    }

    public Boolean verifiedUa(String token, HttpServletRequest request){
        return AuthenticationFilter.getUserAgent(request).equals(getUaClaimFromToken(token));
    }
}