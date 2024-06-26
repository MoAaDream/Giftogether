package com.moadream.giftogether.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moadream.giftogether.message.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
	//나중에 삭제 하고 옮기기 
}
