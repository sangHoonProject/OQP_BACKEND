package com.example.oqp.db.entity;

import com.example.oqp.common.enums.UseYn;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.List;

@Table(name = "content_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
public class Content extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    @Comment("콘텐츠 제목")
    private String title;

    @Column(name = "public_yn", nullable = false, length = 1)
    @Comment("콘텐츠 공개 여부")
    private UseYn publicYn;

    @Column(name = "img_url", nullable = false, length = 1000)
    @Comment("이미지 저장 url")
    private String imgUrl;

    // TODO 카테고리 enum 클래스로 빼기
    @Column(name = "category", nullable = false, length = 50)
    @Comment("카테고리")
    private String category;

    @Column(name = "ttl_crr_cnt", length = 50)
    @Comment("콘텐츠 문제 총 맞춘 개수")
    private Integer totalCurrentCount = 0;

    // 콘텐츠 공개 여부 비공개 설정시 코드를 발급함
    @Column(name = "auth_code", length = 100)
    @Comment("비공개 접속 코드")
    private String authCode;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 방식 사용
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    // 지연 로딩 설정 & 콘텐츠 삭제시 콘텐츠와 관련된 모든 문제들 삭제
    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Problem> problem;
}
