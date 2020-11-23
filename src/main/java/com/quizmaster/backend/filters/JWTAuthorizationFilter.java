package com.quizmaster.backend.filters;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.quizmaster.backend.entities.User;
import com.quizmaster.backend.repositories.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private GoogleIdTokenVerifier verifier = null;

    @Autowired
    private Environment environment;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, String googleClientId) {
        super(authenticationManager);
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    private boolean isExcludedPath(String path) {
        List<String> allowedPath = Arrays.asList("/ws");
        for (String currentPath : allowedPath) {
            if (currentPath.equals(path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String var_name = environment.getProperty("DisabledSec");
        if (var_name.equals("true")){
            chain.doFilter(request, response);
            return;
        }

        String headerName = "Authorization";
        String tokenPrefix = "Bearer ";

        //Path where the filter shouldn't apply on
        String path = '/' + request.getRequestURI().split("/")[1];

        if (isExcludedPath(path)) {
            chain.doFilter(request, response);
            return;
        }
        String header = request.getHeader(headerName);
        if (header != null && header.startsWith(tokenPrefix)) {
            String token = header.replace(tokenPrefix, "");
            try {
                GoogleIdToken idToken = verifier.verify(token);
                if (idToken != null) {
                    Payload payload = idToken.getPayload();
                    saveUserIfNew(payload);
                    chain.doFilter(request, response);
                } else {
                    response.sendError(401, "Token is not valid. ");
                }
            } catch (GeneralSecurityException | IOException e) {
                response.sendError(401, "Authorization failed. " + e.getMessage());
            }
            return;
        }
        response.sendError(401, "Authorization token not defined as expected. ");
    }


    @Autowired
    private UserMongoRepository userMongoRepository;

    void saveUserIfNew(Payload payload) {
        String userId = payload.getSubject();
        System.out.println("Email :" + payload.getEmail());
        System.out.println("UserID :" + userId);

        if (!userMongoRepository.existsUserByEmail(payload.getEmail())) {
            System.out.println("New user registered: " + payload.getEmail() + " - " + userId);
            userMongoRepository.save(new User(payload.getEmail(), userId));
        } else System.out.println("Already known user: " + payload.getEmail() + " - " + userId);
    }
}
