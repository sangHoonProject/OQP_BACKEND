package com.example.oqp.common.custom;

import com.example.oqp.db.entity.UserInfo;
import com.example.oqp.db.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username : {}", username);
        UserInfo userInfo = userInfoRepository.findByEmail(username);

        if(userInfo == null){
            throw new RuntimeException("사용자를 찾지 못했습니다.");
        }

        return new CustomUserDetails(userInfo);
    }
}
