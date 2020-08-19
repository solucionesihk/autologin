package com.tdp.appconvergente.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

//  @GetMapping
//  public String home(ExpiringUsernameAuthenticationToken userToken) {
//  public String home(ExpiringUsernameAuthenticationToken userToken) {
//    User user = (User) userToken.getPrincipal();
//    return String.format("Welcome %s", user.getId());
//    return "index";
//  }
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/test")
    public String test() {
        return "test";
    }
}
