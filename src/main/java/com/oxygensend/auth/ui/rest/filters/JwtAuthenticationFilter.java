package com.oxygensend.auth.ui.rest.filters;

import com.oxygensend.auth.application.token.TokenApplicationService;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.AccessTokenPayload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORITIES_ATTRIBUTE = "X-AUTHORITIES";
    private static final String USER_ID_ATTRIBUTE = "X-USER-ID";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final UserDetailsService userDetailsService;
    private final TokenApplicationService jwtFacade;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, TokenApplicationService jwtFacade) {
        this.userDetailsService = userDetailsService;
        this.jwtFacade = jwtFacade;
    }

    @Override
    public void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(7);
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (securityContext.getAuthentication() == null) {
            AccessTokenPayload tokenPayload = (AccessTokenPayload) jwtFacade.parseToken(jwtToken, TokenType.ACCESS);
            if (tokenPayload.exp().after(new Date())) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(tokenPayload.email().address());
                UsernamePasswordAuthenticationToken authToken = getAuthToken(userDetails, request);
                securityContext.setAuthentication(authToken);

                request.setAttribute(USER_ID_ATTRIBUTE, tokenPayload.businessId());
                request.setAttribute(AUTHORITIES_ATTRIBUTE, userDetails.getAuthorities());
            }
            filterChain.doFilter(request, response);
        }

    }

    private UsernamePasswordAuthenticationToken getAuthToken(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        return authToken;
    }
}
