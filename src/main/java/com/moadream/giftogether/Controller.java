package com.moadream.giftogether;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {

    @GetMapping("/member")
    public String getMember(){
        return "hello";
    }
    
    @GetMapping("/")
    public String getLayout(){
        return "redirect:/login";
    }
    @GetMapping("/ex")
    public String getEx(){
        return "example";
    }
}