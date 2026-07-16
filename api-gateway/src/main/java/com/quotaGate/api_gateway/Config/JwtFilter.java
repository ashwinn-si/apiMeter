package com.quotaGate.api_gateway.Config;

import com.quotaGate.api_gateway.DTO.JwtDTO;
import com.quotaGate.api_gateway.Service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().contains("public");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {

            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                unauthorized(response, "Missing or invalid Authorization header");
                return;
            }

            String token = authorizationHeader.substring(7).trim();

            if (token.isBlank()) {
                unauthorized(response, "Token is empty");
                return;
            }

            if (!tokenService.checkToken(token)) {
                unauthorized(response, "Invalid token");
                return;
            }

            JwtDTO jwtDTO = tokenService.getJwtClaims(token);

            HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {

                @Override
                public String getHeader(String name) {
                    if ("X-User-Email".equalsIgnoreCase(name)) {
                        return jwtDTO.getEmail();
                    }
                    if ("X-User-Id".equalsIgnoreCase(name)) {
                        return String.valueOf(jwtDTO.getId());
                    }
                    return super.getHeader(name);
                }

                @Override
                public Enumeration<String> getHeaders(String name) {
                    if ("X-User-Email".equalsIgnoreCase(name)) {
                        return Collections
                                .enumeration(Collections.singletonList(jwtDTO.getEmail()));
                    }
                    if ("X-User-Id".equalsIgnoreCase(name)) {
                        return Collections.enumeration(
                                Collections.singletonList(String.valueOf(jwtDTO.getId())));
                    }
                    return super.getHeaders(name);
                }

                @Override
                public Enumeration<String> getHeaderNames() {
                    Set<String> names =
                            new LinkedHashSet<>(Collections.list(super.getHeaderNames()));
                    names.add("X-User-Email");
                    names.add("X-User-Id");
                    return Collections.enumeration(names);
                }
            };

            filterChain.doFilter(requestWrapper, response);

        } catch (Exception e) {
            unauthorized(response, "Invalid token");
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
                {
                  "status":401,
                  "error":"%s"
                }
                """.formatted(message));
    }
}
