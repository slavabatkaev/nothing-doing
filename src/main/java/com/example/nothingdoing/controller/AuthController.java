// src/main/java/com/example/nothingdoing/controller/AuthController.java
package com.example.nothingdoing.controller;

import com.example.nothingdoing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        if (userService.register(username, password)) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Пользователь уже существует");
            return "register";
        }
    }
}