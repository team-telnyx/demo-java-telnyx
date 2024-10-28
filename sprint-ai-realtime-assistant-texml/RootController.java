package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;

@RestController
public class RootController {
    @GetMapping("/")
    public Map<String, String> root() {
        return Collections.singletonMap("message", "Telnyx Media Stream Server is running!");
    }
}