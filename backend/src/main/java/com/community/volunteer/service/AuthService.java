package com.community.volunteer.service;

import com.community.volunteer.dto.auth.LoginRequest;
import com.community.volunteer.vo.auth.LoginVO;
import com.community.volunteer.vo.auth.UserVO;

public interface AuthService {

    LoginVO login(LoginRequest request);

    UserVO currentUser(String token);
}
