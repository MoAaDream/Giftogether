package com.moadream.giftogether.message.repository;

import com.moadream.giftogether.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
	//나중에 삭제 하고 옮기기

    List<Message> findAllByWishlist_Link(String wishlist_link);
}
