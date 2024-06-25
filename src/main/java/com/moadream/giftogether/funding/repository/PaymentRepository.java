package com.moadream.giftogether.funding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moadream.giftogether.funding.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}