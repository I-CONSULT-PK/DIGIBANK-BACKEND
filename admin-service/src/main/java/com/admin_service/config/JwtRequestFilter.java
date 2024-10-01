package com.admin_service.config;
import com.admin_service.exception.CustomJwtException;
import com.admin_service.model.CustomResponseEntity;
import com.admin_service.service.serviceImpl.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;

import java.io.IOException;
import java.security.SignatureException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final JwtUtil jwtUtil;
    private final MyUserDetailsService myUserDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil, MyUserDetailsService myUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                // Attempt to extract username from the token
                Claims claims = jwtUtil.getClaims(jwt);
                username = claims.getSubject();
            }
            catch (ExpiredJwtException e) {
                // If token is expired in the JWT payload, return appropriate response
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT token has expired. Please login again.");
                LOGGER.error(e.getMessage());
                response.getWriter().flush();
                return;

            } catch (CustomJwtException e) {
                // Handle all other invalid token cases (invalid format, etc.)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(e.getMessage());
                LOGGER.error(e.getMessage());
                response.getWriter().flush();
                return;
            }
        }

        // Continue processing if username is extracted and token is valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(username);

            if (userDetails != null && jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                List<SimpleGrantedAuthority> authorities = userDetails.getAuthorities()
                        .stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized access.");
                LOGGER.info("Unauthorized access.");
                response.getWriter().flush();
                return;
            }
        }

        // Proceed with the filter chain for other valid requests
        chain.doFilter(request, response);
    }
}