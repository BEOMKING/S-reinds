package com.project.autonomous.user.service;

import com.project.autonomous.common.exception.CustomException;
import com.project.autonomous.common.exception.ErrorCode;
import com.project.autonomous.jwt.TokenProvider;
import com.project.autonomous.jwt.dto.TokenDto;
import com.project.autonomous.jwt.dto.TokenRequestDto;
import com.project.autonomous.jwt.entity.RefreshToken;
import com.project.autonomous.jwt.repository.RefreshTokenRepository;
import com.project.autonomous.picture.entity.Picture;
import com.project.autonomous.picture.service.DBFileStorageService;
import com.project.autonomous.user.dto.request.LoginReq;
import com.project.autonomous.user.dto.request.UserRegisterReq;
import com.project.autonomous.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DBFileStorageService dbFileStorageService;

    @Transactional
    public TokenDto signup(UserRegisterReq userRegisterReq) throws IOException {
        // 가입되어있는지 확인 (회원을 삭제해도 DB에 회원 정보가 남아있어서 가입 안됨 고민해야할 일)
        if (userRepository.existsByEmail(userRegisterReq.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_JOIN);
        }
        Picture picture;
        if(userRegisterReq.getFile() == null) picture = null;
        else picture = dbFileStorageService.storeFile(userRegisterReq.getFile());
        userRepository.save(userRegisterReq.toUser(passwordEncoder, picture));

        return login(new LoginReq(userRegisterReq.getEmail(), userRegisterReq.getPassword()));
    }

    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public TokenDto login(LoginReq loginReq) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginReq.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
            .key(authentication.getName())
            .value(tokenDto.getRefreshToken())
            .build();

        refreshTokenRepository.save(refreshToken);

        // 5. 토큰 발급
        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 User ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(
            tokenRequestDto.getAccessToken());

        // 3. 저장소에서 User ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
            .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return tokenDto;
    }
}
