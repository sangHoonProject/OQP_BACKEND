package com.example.oqp.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Column(name = "reg_dt",updatable = false, nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime regDt;

    @LastModifiedDate
    @Column(name = "chg_dt", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime chgDt;
}
