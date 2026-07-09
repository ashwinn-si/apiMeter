package com.quotaGate.api_gateway.Config;

import com.quotaGate.api_gateway.DTO.JwtDTO;
import com.quotaGate.api_gateway.Service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean shouldNotFilter(HttpServletRequest request){
        return request.getRequestURI().contains("public");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authorizationHeader.substring(7).strip();

        if(token.length() == 0){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if(!tokenService.checkToken(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
                    return Collections.enumeration(Collections.singletonList(jwtDTO.getEmail()));
                }
                if ("X-User-Id".equalsIgnoreCase(name)) {
                    return Collections.enumeration(Collections.singletonList(String.valueOf(jwtDTO.getId())));
                }
                return super.getHeaders(name);
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                List<String> names = Collections.list(super.getHeaderNames());
                names.add("X-User-Email");
                names.add("X-User-Id");
                return Collections.enumeration(names);
            }
        };

        filterChain.doFilter(requestWrapper, response);
    }
}
