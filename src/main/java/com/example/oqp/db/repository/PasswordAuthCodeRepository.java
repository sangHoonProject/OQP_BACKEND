package com.example.oqp.db.repository;

import com.example.oqp.common.enums.UseYn;
import com.example.oqp.db.entity.PasswordAuthCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordAuthCodeRepository extends JpaRepository<PasswordAuthCode, Long> {
    List<PasswordAuthCode> findByEmailAndUseYn(String email, UseYn useYn);
}
