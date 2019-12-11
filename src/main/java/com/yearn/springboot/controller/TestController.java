package com.yearn.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhoub on 2019/12/11.
 */
@RestController //相对与 @Controller + @ResponseBody
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "test";
    }

}
