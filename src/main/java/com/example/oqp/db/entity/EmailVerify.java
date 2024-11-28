package com.example.oqp.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Table(name = "eml_ver")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EmailVerify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "eml_code", length = 45, nullable = false)
    @Comment("이메일 인증코드")
    private String emlCode;
}
