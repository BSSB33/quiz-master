package com.quizmaster.backend.filters;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private GoogleIdTokenVerifier verifier = null;


    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, String googleClientId) {
        super(authenticationManager);
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // super.doFilterInternal(request, response, chain);
        String headerName = "Authorization";
        String tokenPrefix = "Bearer ";


        String path = '/' + request.getRequestURI().split("/")[1];
        //Path where the filter shouldn't apply on

        System.out.println(path);
        if ("/ws".equals(path) || "/newid".equals(path) || "/results".equals(path) || "/create".equals(path)) {
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
                    String userId = payload.getSubject();
//                    System.out.println(userId);
                    // TODO: userId should be stored somewhere.
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
}
