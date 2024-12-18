package com.example.oqp.db.entity;

import com.example.oqp.common.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "user_info")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 150)
    @Comment("사용자 이메일")
    private String email;

    @Column(name = "nickname", nullable = false, length = 50)
    @Comment("닉네임")
    private String nickname;

    @Column(name = "password", nullable = false, length = 100)
    @Comment("비밀번호")
    private String password;

    @Column(name = "role", nullable = false, length = 10)
    @Comment("권한")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "ttl_heart_cnt")
    @Comment("총 하트 수")
    private Integer totalHeartCount;

    @Column(name = "content_id")
    @Comment("콘텐츠 테이블 기본키")
    private Long contentId;
}
