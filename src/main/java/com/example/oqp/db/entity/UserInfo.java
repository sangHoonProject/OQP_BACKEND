package com.example.oqp.db.entity;

import com.example.oqp.common.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Table(name = "user_info")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    @Comment("사용자 이메일")
    private String email;

    @Column(name = "nickname", nullable = false)
    @Comment("닉네임")
    private String nickname;

    @Column(name = "password", nullable = false)
    @Comment("비밀번호")
    private String password;

    @Column(name = "role", nullable = false)
    @Comment("권한")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "reg_dt", nullable = false)
    @Comment("가입 일시")
    private LocalDateTime regDt;
}
