package com.krishnanityadas.task_management.config;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.krishnanityadas.task_management.service.JwtService;
import com.krishnanityadas.task_management.service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path != null && (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")
                || path.startsWith("/login/oauth2") || path.startsWith("/error")
                || path.startsWith("/oauth2") || path.startsWith("/favicon.ico"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String rawToken = jwtService.extractTokenFromCookie(request);
        String token = rawToken == null ? null : rawToken.trim();

        if (token == null || token.isBlank()) {
            request.setAttribute(SecurityErrorAttributes.AUTH_FAILURE_REASON,
                    "Missing authentication cookie 'token'.");
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String userName = jwtService.extractUserName(token);
                if (userName != null && !userName.isBlank()) {
                    UserDetails userDetails = context.getBean(MyUserDetailsService.class)
                            .loadUserByUsername(userName);
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null,
                                        userDetails.getAuthorities());
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        request.setAttribute(SecurityErrorAttributes.AUTH_FAILURE_REASON,
                                "Invalid or expired token.");
                        logger.debug("JWT validation failed for user '{}' on path '{}'", userName,
                                request.getServletPath());
                    }
                } else {
                    request.setAttribute(SecurityErrorAttributes.AUTH_FAILURE_REASON,
                            "Invalid token: subject (username) is missing.");
                }
            } catch (Exception ex) {
                request.setAttribute(SecurityErrorAttributes.AUTH_FAILURE_REASON,
                        "Invalid token: " + ex.getMessage());
                logger.debug("JWT authentication skipped on path '{}': {}",
                        request.getServletPath(), ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);

    }



}
