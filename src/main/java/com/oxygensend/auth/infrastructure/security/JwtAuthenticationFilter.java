package com.oxygensend.auth.infrastructure.security;

import com.oxygensend.auth.context.jwt.JwtFacade;
import com.oxygensend.auth.context.jwt.payload.AccessTokenPayload;
import com.oxygensend.auth.domain.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORITIES_ATTRIBUTE = "X-AUTHORITIES";
    private static final String USER_ID_ATTRIBUTE = "X-USER-ID";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final UserDetailsService userDetailsService;
    private final JwtFacade jwtFacade;

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
            AccessTokenPayload tokenPayload = (AccessTokenPayload) jwtFacade.validateToken(jwtToken, TokenType.ACCESS);
            if (tokenPayload.exp().after(new Date())) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(tokenPayload.identity());
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
