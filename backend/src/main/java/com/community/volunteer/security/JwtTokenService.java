package com.community.volunteer.security;

import com.community.volunteer.common.BusinessException;
import com.community.volunteer.common.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtTokenService {

    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Long userId, String username, String realName, String role) {
        if (jwtProperties.secret() == null || jwtProperties.secret().length() < 32) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "JWT密钥长度不足");
        }
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuer(jwtProperties.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.expireMinutes(), ChronoUnit.MINUTES)))
                .claims(Map.of(
                        "userId", userId,
                        "realName", realName,
                        "role", role
                ))
                .signWith(secretKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String authorizationHeader) {
        String token = cleanAuthorizationHeader(authorizationHeader);
        Number userId = parseToken(token).get("userId", Number.class);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "令牌无效");
        }
        return userId.longValue();
    }

    public String getRole(String authorizationHeader) {
        String token = cleanAuthorizationHeader(authorizationHeader);
        String role = parseToken(token).get("role", String.class);
        if (role == null || role.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "令牌无效");
        }
        return role;
    }

    private String cleanAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未携带令牌");
        }
        String rawToken = authorizationHeader.replaceFirst("(?i)^Bearer\\s+", "").trim();
        if (rawToken.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "令牌无效");
        }
        return rawToken;
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }
}
