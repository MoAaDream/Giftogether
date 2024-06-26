package com.moadream.giftogether.global.schedule.config;

import com.moadream.giftogether.wishlist.service.WishListServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final WishListServiceI wishListService;


    @Scheduled(cron = "${myCustom.cron}")
    public void wishlistStatusUpdate(){
        log.info("업데이트 메서드 실행");
        wishListService.updateWishListStatus();
        log.info("업데이트 메서드 종료");
    }


}
