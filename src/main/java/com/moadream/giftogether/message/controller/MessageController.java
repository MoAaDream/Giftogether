package com.moadream.giftogether.message.controller;

import org.springframework.stereotype.Controller;

import com.moadream.giftogether.message.service.MessageService;
import com.moadream.giftogether.wishlist.repository.WishListRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
public class MessageController {
	
	MessageService messageService;
	WishListRepository wishListRepository;


}
