package com.example.oqp.db.repository;

import com.example.oqp.db.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findByEmail(String email);

    boolean existsByEmail(String email);
}
