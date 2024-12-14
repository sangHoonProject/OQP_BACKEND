package com.example.oqp.db.entity;

import com.example.oqp.common.enums.UseYn;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "jwt_refresh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JwtRefresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", length = 2000, unique = true)
    @Comment("refresh 토큰")
    private String token;

    @Column(name = "use_yn", length = 1)
    @Comment("사용 여부")
    @Enumerated(EnumType.STRING)
    private UseYn useYn;

    @Column(name = "user_id")
    @Comment("사용자 id")
    private Long userId;

    @Column(name = "expired_at", columnDefinition = "TIMESTAMP")
    @Comment("만료 시간")
    private LocalDateTime expiredAt;
}
