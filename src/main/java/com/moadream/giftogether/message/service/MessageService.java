package com.moadream.giftogether.message.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moadream.giftogether.message.model.Message;
import com.moadream.giftogether.message.model.MessageFundDto;
import com.moadream.giftogether.message.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

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

    @Transactional(readOnly = true)
    public List<MessageFundDto> getMessageFundingProduct(String productLink){
        List<Message> allByProductLink = messageRepository.findAllByProduct_Link(productLink);

        List<MessageFundDto> messageFundDtoList = new ArrayList<>();

        for(Message message : allByProductLink){
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
