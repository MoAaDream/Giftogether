package com.moadream.giftogether.message.service;

import com.moadream.giftogether.member.model.Member;
import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.message.model.MessageFundDto;
import com.moadream.giftogether.message.repository.MessageRepository;
import com.moadream.giftogether.wishlist.model.WishList;
import com.moadream.giftogether.wishlist.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public List<MessageFundDto> getMessageFunding(String wishlistLink){
        List<Message> allByWishlistLink = messageRepository.findAllByWishlist_Link(wishlistLink);

        List<MessageFundDto> messageFundDtoList = new ArrayList<>();

        for(Message message : allByWishlistLink){
            MessageFundDto dto = new MessageFundDto();
            dto.setMemberId(message.getFunding().getMember().getId());
            dto.setName(message.getFunding().getMember().getNickname());
            dto.setContent(message.getContent());
            dto.setAmount(message.getFunding().getAmount());
            dto.setFundingUID(message.getFunding().getFundingUid());

            messageFundDtoList.add(dto);
        }

        return messageFundDtoList;
    }
}
