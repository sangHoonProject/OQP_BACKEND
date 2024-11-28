package com.example.oqp.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Table(name = "jwt_sec")
@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jwt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", length = 2000, nullable = false)
    @Comment("refresh 토큰")
    private String token;

    @Column(name = "is_revoked", nullable = false)
    @Comment("만료 여부")
    private Boolean isRevoked;

    @Column(name = "created_at", nullable = false)
    @Comment("생성 일시")
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    @Comment("만료 일시")
    private LocalDateTime expiredAt;

    @JoinColumn(name = "user_info_id")
    @ManyToOne
    private User user;
}
