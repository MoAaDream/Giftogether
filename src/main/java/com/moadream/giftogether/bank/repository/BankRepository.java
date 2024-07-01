package com.moadream.giftogether.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moadream.giftogether.bank.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
}