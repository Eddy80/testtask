package com.mytest.testtask.repositories;

import com.mytest.testtask.models.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    Optional<Subscriber> findByMsisdn(String  msisdn);
}
