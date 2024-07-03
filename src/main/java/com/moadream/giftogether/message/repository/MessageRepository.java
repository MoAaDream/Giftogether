package com.moadream.giftogether.message.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moadream.giftogether.Status;
import com.moadream.giftogether.message.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	
    List<Message> findAllByWishlist_Link(String wishlist_link);
    List<Message> findAllByWishlist_LinkAndStatus(String wishlist_link, Status status);

	Message findByFundingIdAndStatus(Long fundingId,Status status);

}
