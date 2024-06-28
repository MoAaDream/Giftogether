package com.moadream.giftogether.message.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moadream.giftogether.message.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	//나중에 삭제 하고 옮기기

    List<Message> findAllByWishlist_Link(String wishlist_link);

	List<Message> findAllByProduct_Link(String productLink);

}
