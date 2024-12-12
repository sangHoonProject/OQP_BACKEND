package com.example.oqp.db.repository;

import com.example.oqp.db.entity.JwtRefresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JwtRefreshRepository extends JpaRepository<JwtRefresh, Long> {
    @Query(value = "SELECT * FROM jwt_refresh WHERE user_id = :userId AND use_yn = 'Y'", nativeQuery = true)
    List<JwtRefresh> findByUserId(Long userId);
}
