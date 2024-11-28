package com.example.oqp.db.repository;

import com.example.oqp.db.entity.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtRepository extends JpaRepository<Jwt, Long> {
}
