package com.project.autonomous.user.service;

import com.project.autonomous.jwt.dto.TokenDto;
import com.project.autonomous.jwt.dto.TokenRequestDto;
import com.project.autonomous.user.dto.request.LoginReq;
import com.project.autonomous.user.dto.request.UserRegisterReq;

import java.io.IOException;

public interface AuthService {

    TokenDto signup(UserRegisterReq userRegisterReq) throws IOException;

    boolean checkEmail(String email);

    TokenDto login(LoginReq loginReq);

    TokenDto reissue(TokenRequestDto tokenRequestDto);

}
