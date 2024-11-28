package com.example.oqp.db.entity;

import com.example.oqp.auth.jwt.db.entity.Jwt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "user_info")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 기본키")
    private Long id;

    @Column(name = "email", length = 150, nullable = false)
    @Comment("이메일")
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    @Comment("비밀번호")
    private String password;

    @Column(name = "name", length = 45)
    @Comment("이름")
    private String name;

    @Column(name = "reg_dt")
    @Comment("등록 일시")
    private LocalDateTime regDt;

    @Column(name = "chg_dt")
    @Comment("수정 일시")
    private LocalDateTime chgDt;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Jwt> jwts;
}
