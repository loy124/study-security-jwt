package com.onyou.firstproject.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("hello")
    public String hello(){
        return "HELLOa";
    }

    @GetMapping("hello2")
    public String hello2(){
        return "HELLO2";
    }

}
