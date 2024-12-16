package com.example.oqp.db.entity;

import com.example.oqp.common.enums.UseYn;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@ToString
@Entity
@Table(name = "problem_info")
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem", nullable = false, length = 100)
    @Comment("문제")
    private String problem;

    @Column(name = "crr_ansr", length = 100)
    @Comment("정답")
    private String currentAnswer;

    @Column(name = "img_url", length = 1000)
    @Comment("문제 이미지 url")
    private String imgUrl;

    @Column(name = "crr_yn", length = 1, nullable = false)
    @Comment("정답 여부")
    private UseYn currentYn;

    // 지연 로딩 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;
}
