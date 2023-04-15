package com.example.vse_back.configuration.jwt;

import com.example.vse_back.configuration.CustomUserDetails;
import com.example.vse_back.configuration.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static org.springframework.util.StringUtils.hasText;

@Component
@Log
public class JwtFilter extends GenericFilterBean {
    @Value("${jwt.header}")
    private String jwtHeader;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("do filter...");
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if (token != null && jwtProvider.validateToken(token)) {
            String userId = jwtProvider.getUserIdFromToken(token);
            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(userId);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(jwtHeader);
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
