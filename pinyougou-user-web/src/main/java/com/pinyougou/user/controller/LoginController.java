package com.pinyougou.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getUsername")
    public Map<String, String> getUserName() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("username", username);
        return userMap;
    }
}
