package com.community.volunteer.vo.auth;

public record UserVO(
        Long id,
        String username,
        String realName,
        String phone,
        Integer status
) {
}
