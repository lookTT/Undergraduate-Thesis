package com.community.volunteer.security;

public record JwtPrincipal(
        Long userId,
        String username,
        String realName,
        String role
) {
}
