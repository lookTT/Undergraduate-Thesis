package com.community.volunteer.vo.auth;

import java.util.List;

public record LoginVO(
        String token,
        Long userId,
        String username,
        String realName,
        List<String> roles
) {
}
