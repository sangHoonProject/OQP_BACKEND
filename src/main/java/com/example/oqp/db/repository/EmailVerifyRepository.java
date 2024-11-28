package com.example.oqp.db.repository;

import com.example.oqp.db.entity.EmailVerify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Long> {
}
