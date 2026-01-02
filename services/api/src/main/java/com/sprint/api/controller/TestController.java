package com.sprint.api.controller; // 위치에 맞게 패키지 선언

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/hello")
    public String hello() {
        return "API Server OK!";
    }
}