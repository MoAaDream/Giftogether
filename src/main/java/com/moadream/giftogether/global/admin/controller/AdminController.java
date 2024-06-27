package com.moadream.giftogether.global.admin.controller;

import com.moadream.giftogether.global.admin.dto.StaticsDto;
import com.moadream.giftogether.global.admin.service.AdminService;
import com.moadream.giftogether.global.exception.SessionNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.moadream.giftogether.global.exception.GlobalExceptionCode.SESSION_NOT_FOUND;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/statics")
    public String getAdminPage(HttpSession session, Model model){
        String socialId = checkSession(session);

        adminService.CheckAdmin(socialId);

        StaticsDto staticsDto = adminService.getStatics();

        model.addAttribute("data", staticsDto);

        return "admin/statics";
    }

    private String checkSession(HttpSession session) {
        if (session == null)
            throw new SessionNotFoundException(SESSION_NOT_FOUND);

        if (session.getAttribute("kakaoId") == null)
            throw new SessionNotFoundException(SESSION_NOT_FOUND);

        return session.getAttribute("kakaoId").toString();
    }
}
