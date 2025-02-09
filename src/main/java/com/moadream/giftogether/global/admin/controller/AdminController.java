package com.moadream.giftogether.global.admin.controller;

import static com.moadream.giftogether.global.exception.GlobalExceptionCode.SESSION_NOT_FOUND;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.moadream.giftogether.global.admin.dto.StaticsDto;
import com.moadream.giftogether.global.admin.service.AdminService;
import com.moadream.giftogether.global.exception.SessionNotFoundException;
import com.moadream.giftogether.member.model.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    

    @GetMapping("")
    public String adminStart(HttpSession session, Model model){
        String socialId = checkSession(session);

        adminService.checkAdmin(socialId);

        return "admin/index";
    }



    @GetMapping("/statics")
    public String getAdminPage(HttpSession session, Model model){
        String socialId = checkSession(session);

        adminService.checkAdmin(socialId);

        StaticsDto staticsDto = adminService.getStatics();

        model.addAttribute("data", staticsDto);

        return "admin/statics";
    }


    @GetMapping("/blacklist")
    public String getBlackListPage(HttpSession session, Model model){
        String socialId = checkSession(session);

        adminService.checkAdmin(socialId);

        List<Member> memberList = adminService.getBlackListMember();

        model.addAttribute("data", memberList);

        return "admin/blacklist";
    }

    @PostMapping("/blacklist/{member_id}")
    public String getBlackListPage(HttpSession session, @PathVariable("member_id") String memberId){
        String socialId = checkSession(session);
        adminService.checkAdmin(socialId);
        adminService.removeBlackList(Integer.parseInt(memberId));
        log.info("Member blacklist removed");

        return "redirect:/admin/blacklist";
    }

    private String checkSession(HttpSession session) {
        if (session == null)
            throw new SessionNotFoundException(SESSION_NOT_FOUND);

        if (session.getAttribute("kakaoId") == null)
            throw new SessionNotFoundException(SESSION_NOT_FOUND);

        return session.getAttribute("kakaoId").toString();
    }
}
