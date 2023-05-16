package jp.co.axa.apidemo.filter;

import jp.co.axa.apidemo.entities.Token;
import jp.co.axa.apidemo.repositories.TokenRepo;
import jp.co.axa.apidemo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//will fire everytime a request comes in and it will only filter it once
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepo tokenRepo;

    @Value("${request.options}")
    private String preflightRequest;

    @Value("${token.prefix}")
    private String tokenPrefix;


    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider, TokenRepo tokenRepo) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepo = tokenRepo;
    }

    //check whether the user is valid and the token is valid and then set that user as the authenticated user
    //there is a filter chain parameter that will have to be called, when done pass data to the next filter in the chain
    //I want to ignore options request(gets sent before every request) because they are just preflight
    //request(gathers information about the server, like stuff about headers etc.) to see what the server will accept before
    //sending the actual request which would be a GET, POST, PUT, DELETE etc.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //if the request is options, I let it go through this filter, if not I run further checks on the request
        if(request.getMethod().equalsIgnoreCase(preflightRequest)){
            //set response status to 200
            response.setStatus(HttpStatus.OK.value());
            //send the request down the filter chain to next filter
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            //checks if the correct auth header value is present
            if(authorizationHeader == null || !authorizationHeader.startsWith(tokenPrefix)){
                filterChain.doFilter(request,response);
                return;
            }

            //strips bearer prefix from token
            String token = authorizationHeader.replace(tokenPrefix, "").replace(" ", "");
            System.out.println(token);

            //if token has been tampered with it will throw an exception
            String username = jwtTokenProvider.getSubject(token);
            Token tokenValue = tokenRepo.findTokenByTokenValue(token);

            //make sure token is valid and the context is not already set(make sure user is not already in the context)
            //makes sure that ip and user agent is the same of the one that was issued a token
            //checks if token is in the blacklist, if it is it rejects it.
            if(jwtTokenProvider.isTokenValid(username, token) && SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtTokenProvider.verifiedIp(token, request) && jwtTokenProvider.verifiedUa(token, request) && tokenValue == null){

                //I will now set them in the security context
                List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);

                Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);

                //set authentication for user in the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                //if any checks that I performed produces an exception, I clear the security context,
                // makes sure nothing is lingering in the context
                SecurityContextHolder.clearContext();
            }
        }

        //let the request go down the filter chain
        filterChain.doFilter(request, response);
    }
}