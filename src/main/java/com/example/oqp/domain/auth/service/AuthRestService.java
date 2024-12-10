package com.example.oqp.domain.auth.service;

import com.example.oqp.common.enums.Role;
import com.example.oqp.common.error.CustomException;
import com.example.oqp.common.error.ErrorCode;
import com.example.oqp.common.jwt.JwtTokenResponse;
import com.example.oqp.common.jwt.JwtUtil;
import com.example.oqp.db.entity.UserInfo;
import com.example.oqp.db.repository.UserInfoRepository;
import com.example.oqp.domain.auth.restcontroller.request.LoginRequest;
import com.example.oqp.domain.auth.restcontroller.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthRestService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserInfo register(RegisterRequest registerRequest) {

        if(userInfoRepository.existsByEmail(registerRequest.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_USED_EMAIL);
        }

        UserInfo userInfo = toUserInfo(registerRequest);
        return userInfoRepository.save(userInfo);
    }

    private UserInfo toUserInfo(RegisterRequest registerRequest) {
        return UserInfo.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .nickname(registerRequest.getNickname())
                .role(Role.ROLE_USER)
                .regDt(LocalDateTime.now())
                .build();
    }

    public JwtTokenResponse login(LoginRequest loginRequest) {

        try{
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            return jwtUtil.generateToken(authentication);
        }catch (Exception e){
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

    }
}
