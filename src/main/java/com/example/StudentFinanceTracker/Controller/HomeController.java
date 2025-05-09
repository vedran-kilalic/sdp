package com.example.StudentFinanceTracker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "pages/login";
    }

    @GetMapping("/student")
    public String index() {
        return "index";
    }
    @GetMapping("/admin")
    public String adminIndex() {
        return "indexAdmin";
    }

}
