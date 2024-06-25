package com.moadream.giftogether.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moadream.giftogether.message.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	
	
}