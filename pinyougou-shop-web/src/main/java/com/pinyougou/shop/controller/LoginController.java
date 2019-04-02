package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getUsername")
    public Map getName(){
        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        HashMap<String, String> map = new HashMap<>();
        map.put("username",username);
        return map;
    }


}
