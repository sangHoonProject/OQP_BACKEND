package com.example.oqp.db.repository;

import com.example.oqp.common.enums.UseYn;
import com.example.oqp.db.entity.MailCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MailCodeRepository extends JpaRepository<MailCode, Long> {

    @Query("select f from MailCode f where f.email = :email and f.useYn = :useYn")
    List<MailCode> findByEmailWhereUseYn(String email, UseYn useYn);

    @Query("select f from MailCode f where f.email = :email and f.code = :code")
    Optional<MailCode> findByEmailAndCode(String email, String code);
}
