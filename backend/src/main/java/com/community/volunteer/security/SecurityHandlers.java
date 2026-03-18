package com.community.volunteer.security;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.common.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Configuration
public class SecurityHandlers {

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return (request, response, authException) -> write(response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED,
                ResultCode.UNAUTHORIZED.getCode(), authException.getMessage());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return (request, response, accessDeniedException) -> write(response, objectMapper, HttpServletResponse.SC_FORBIDDEN,
                ResultCode.FORBIDDEN.getCode(), accessDeniedException.getMessage());
    }

    private void write(HttpServletResponse response, ObjectMapper objectMapper, int status, int code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(code, message));
    }
}
