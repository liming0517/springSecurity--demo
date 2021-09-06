package com.springSecurityResource.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/*应用访问目录*/
@RestController
public class MemberService {
    @GetMapping("/getMember")
    public String getMember() {
        return "我是会员服务接口";
    }
}