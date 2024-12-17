package com.example.oqp.db.entity;

import com.example.oqp.common.enums.UseYn;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Table(name = "mail_code")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class MailCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 10, unique = true)
    @Comment("인증 코드")
    private String code;

    @Column(name = "email", nullable = false, length = 150)
    @Comment("사용자 이메일")
    private String email;

    @Column(name = "use_yn", nullable = false, length = 1)
    @Comment("사용 여부")
    @Enumerated(EnumType.STRING)
    private UseYn useYn;
}
