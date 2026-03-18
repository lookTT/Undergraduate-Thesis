package com.community.volunteer.controller.auth;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.dto.auth.LoginRequest;
import com.community.volunteer.service.AuthService;
import com.community.volunteer.security.JwtPrincipal;
import com.community.volunteer.vo.auth.LoginVO;
import com.community.volunteer.vo.auth.UserVO;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/auth/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/api/auth/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }

    @GetMapping("/api/auth/me")
    public ApiResponse<UserVO> me(Authentication authentication) {
        return ApiResponse.success(authService.currentUser((JwtPrincipal) authentication.getPrincipal()));
    }
}
