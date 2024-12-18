package com.example.oqp.domain.auth.service;

import com.example.oqp.common.enums.Role;
import com.example.oqp.common.enums.UseYn;
import com.example.oqp.common.error.CustomException;
import com.example.oqp.common.error.ErrorCode;
import com.example.oqp.common.jwt.JwtTokenResponse;
import com.example.oqp.common.jwt.JwtUtil;
import com.example.oqp.db.entity.JwtRefresh;
import com.example.oqp.db.entity.MailCode;
import com.example.oqp.db.entity.UserInfo;
import com.example.oqp.db.repository.JwtRefreshRepository;
import com.example.oqp.db.repository.MailCodeRepository;
import com.example.oqp.db.repository.UserInfoRepository;
import com.example.oqp.domain.auth.restcontroller.request.LoginRequest;
import com.example.oqp.domain.auth.restcontroller.request.RefreshTokenRequest;
import com.example.oqp.domain.auth.restcontroller.request.RegisterRequest;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthRestService {

    private final UserInfoRepository userInfoRepository;
    private final MailCodeRepository mailCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtRefreshRepository jwtRefreshRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserInfo register(RegisterRequest registerRequest) {

        validateRegisterEmail(registerRequest);

        boolean codeAndEmail = mailCodeRepository.existsByCodeAndEmail(registerRequest.getAuthCode(), registerRequest.getEmail());

        if(codeAndEmail){
            UserInfo userInfo = toUserInfo(registerRequest);

            MailCode mailCode = mailCodeRepository.findByEmailAndCode(registerRequest.getEmail(), registerRequest.getAuthCode())
                    .orElseThrow(() -> new CustomException(ErrorCode.AUTH_CODE_OR_EMAIL_NOT_FOUND));

            mailCode.setUseYn(UseYn.Y);
            mailCodeRepository.save(mailCode);

            return userInfoRepository.save(userInfo);
        }else{
            throw new CustomException(ErrorCode.AUTH_CODE_NOT_FOUND);
        }

    }

    private void validateRegisterEmail(RegisterRequest registerRequest) {

        if(userInfoRepository.existsByEmail(registerRequest.getEmail())) {

            throw new CustomException(ErrorCode.ALREADY_USED_EMAIL);

        }
    }

    private UserInfo toUserInfo(RegisterRequest registerRequest) {
        return UserInfo.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .nickname(registerRequest.getNickname())
                .role(Role.ROLE_USER)
                .totalHeartCount(0)
                .contentId(null)
                .build();
    }

    @Transactional
    public JwtTokenResponse login(LoginRequest loginRequest) {
        try{
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            JwtTokenResponse response = jwtUtil.generateToken(authentication);

            String refresh = response.getRefreshToken();

            Claims claims = jwtUtil.parseClaims(refresh);

            Date expiration = claims.getExpiration();

            LocalDateTime refreshLocalDate = dateToLocalDateTime(expiration);

            UserInfo userInfo = userInfoRepository.findByEmail(loginRequest.getEmail());

            List<JwtRefresh> userRefreshList = jwtRefreshRepository.findByUserId(userInfo.getId());

            JwtRefresh jwtRefresh = toJwtRefresh(refresh, refreshLocalDate, userInfo);

            jwtRefreshRepository.save(jwtRefresh);

            validateUserRefreshList(userRefreshList);

            jwtRefreshRepository.saveAll(userRefreshList);

            return response;

        }catch (Exception e){
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

    }

    private LocalDateTime dateToLocalDateTime(Date expiration) {
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private JwtRefresh toJwtRefresh(String refresh, LocalDateTime refreshLocalDate, UserInfo userInfo) {
        return JwtRefresh.builder()
                .useYn(UseYn.Y)
                .token(refresh)
                .expiredAt(refreshLocalDate)
                .userId(userInfo.getId())
                .build();
    }

    private void validateUserRefreshList(List<JwtRefresh> userRefreshList) {
        if(!userRefreshList.isEmpty()){
            userRefreshList.forEach(user -> {
                if(UseYn.Y.equals(user.getUseYn())){
                    user.setUseYn(UseYn.N);
                }
            });
        }
    }

    @Transactional
    public JwtTokenResponse refresh(RefreshTokenRequest request) {

        JwtRefresh originalRefresh = jwtRefreshRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        validateOriginalRefresh(originalRefresh);

        Authentication authentication = jwtUtil.getAuthentication(request.getRefreshToken());

        JwtTokenResponse response = jwtUtil.generateToken(authentication);

        String refreshToken = response.getRefreshToken();

        Claims claims = jwtUtil.parseClaims(refreshToken);

        Long id = getUserId(claims);

        Date expiration = claims.getExpiration();

        UserInfo userInfo = userInfoRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LocalDateTime expireLocalDate = dateToLocalDateTime(expiration);

        List<JwtRefresh> jwtRefreshList = jwtRefreshRepository.findByUserId(id);

        validateUserRefreshList(jwtRefreshList);

        jwtRefreshRepository.saveAll(jwtRefreshList);

        JwtRefresh jwtRefresh = toJwtRefresh(refreshToken, expireLocalDate, userInfo);

        jwtRefreshRepository.save(jwtRefresh);

        return response;
    }

    private void validateOriginalRefresh(JwtRefresh originalRefresh) {
        if(originalRefresh.getUseYn().equals(UseYn.N)){
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRE);
        }
    }

    private Long getUserId(Claims claims) {
        Integer integerId = (Integer) claims.get("id");
        return Long.valueOf(integerId);
    }
}
