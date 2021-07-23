package com.mytest.testtask.repositories;

import com.mytest.testtask.models.Sms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsRepository extends JpaRepository<Sms, Long> {

    Optional<Sms> findByMsisdn(String  msisdn);

}
