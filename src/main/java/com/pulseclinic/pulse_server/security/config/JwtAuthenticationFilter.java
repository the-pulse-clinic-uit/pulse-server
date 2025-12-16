package com.pulseclinic.pulse_server.security.config;

import com.pulseclinic.pulse_server.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // this is not custom so need to create a custom config file
    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // get the header
        String authHeader = request.getHeader("Authorization");
        // get the jwt
        String jwt;
        // get the user email
        String user_email;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // bearer has  6 letters + space = 7
            jwt = authHeader.substring(7);
            user_email = jwtService.extractUsername(jwt);
            // havent logged in and not null
            if (user_email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(user_email);
                // check if token still valid
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        // else keep on w the chain
        else {
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
