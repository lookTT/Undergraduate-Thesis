package com.community.volunteer.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.community.volunteer.common.BusinessException;
import com.community.volunteer.common.ResultCode;
import com.community.volunteer.dto.auth.LoginRequest;
import com.community.volunteer.entity.Role;
import com.community.volunteer.entity.User;
import com.community.volunteer.entity.UserRole;
import com.community.volunteer.mapper.RoleMapper;
import com.community.volunteer.mapper.UserMapper;
import com.community.volunteer.mapper.UserRoleMapper;
import com.community.volunteer.security.JwtTokenService;
import com.community.volunteer.service.AuthService;
import com.community.volunteer.service.UserService;
import com.community.volunteer.vo.auth.LoginVO;
import com.community.volunteer.vo.auth.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(UserMapper userMapper,
                           UserRoleMapper userRoleMapper,
                           RoleMapper roleMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenService jwtTokenService) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public LoginVO login(LoginRequest request) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, request.username())
                .last("limit 1"));
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getUserId, user.getId()));
        List<String> roles = userRoles.stream()
                .map(UserRole::getRoleId)
                .filter(Objects::nonNull)
                .map(roleMapper::selectById)
                .filter(Objects::nonNull)
                .map(Role::getRoleCode)
                .filter(StringUtils::hasText)
                .toList();

        String primaryRole = roles.isEmpty() ? "VOLUNTEER" : roles.getFirst();
        String token = jwtTokenService.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                primaryRole
        );
        return new LoginVO(token, user.getId(), user.getUsername(), user.getRealName(), roles);
    }

    @Override
    public UserVO currentUser(String token) {
        Long userId = jwtTokenService.getUserId(token);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return new UserVO(user.getId(), user.getUsername(), user.getRealName(), user.getPhone(), user.getStatus());
    }
}
