package com.example.oqp.db.entity;

import com.example.oqp.common.enums.UseYn;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Table(name = "pass_auth_code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class PasswordAuthCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth_code", nullable = false)
    @Comment("인증 코드")
    private String authCode;

    @Column(name = "email", nullable = false)
    @Comment("사용자 이메일")
    private String email;

    @Column(name = "use_yn", nullable = false)
    @Comment("사용 여부")
    @Enumerated(EnumType.STRING)
    private UseYn useYn;
}
