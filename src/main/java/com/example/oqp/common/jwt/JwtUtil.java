package com.example.oqp.common.jwt;

import com.example.oqp.common.custom.CustomUserDetails;
import com.example.oqp.db.entity.UserInfo;
import com.example.oqp.db.repository.UserInfoRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final UserInfoRepository userInfoRepository;

    public JwtUtil(@Value("${jwt.secret}}") String secret, UserInfoRepository userInfoRepository) {
        byte[] bytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(bytes);
        this.userInfoRepository = userInfoRepository;
    }

    public JwtTokenResponse generateToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        String email = authentication.getName();
        UserInfo userInfo = userInfoRepository.findByEmail(email);

        Date accessTokenExpired = new Date(now + 86400000);
        String accessToken = Jwts.builder()
                .setSubject("accessToken")
                .claim("auth", authorities)
                .claim("id", userInfo.getId())
                .setExpiration(accessTokenExpired)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Date refreshTokenExpired = new Date(now + 186400000);
        String refreshToken = Jwts.builder()
                .setSubject("refreshToken")
                .claim("auth", authorities)
                .claim("id", userInfo.getId())
                .setExpiration(refreshTokenExpired)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtTokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if(claims.get("auth") == null){
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        String strId = claims.get("id").toString();
        Long id = Long.valueOf(strId);

        UserInfo userInfo = userInfoRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없음."));

        UserDetails customUserDetails = new CustomUserDetails(userInfo);
        return new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public Claims parseClaims(String token) {
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
