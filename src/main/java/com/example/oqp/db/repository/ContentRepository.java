package com.example.oqp.db.repository;

import com.example.oqp.db.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
