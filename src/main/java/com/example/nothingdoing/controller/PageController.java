package com.example.nothingdoing.controller;

import com.example.nothingdoing.entity.User;
import com.example.nothingdoing.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;


import java.util.Random;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserService userService;

    private final String[] quotes = {
            "Тишина — это не пустота, это полнота.",
            "Неделание — высшая форма действия.",
            "Счастье живёт в паузах между мыслями.",
            "Просто дыши. Всё остальное — лишнее.",
            "Настоящий момент — единственное место, где есть жизнь."
    };

    @GetMapping("/")
    public String index(Model model, Authentication auth) {
        String quote = quotes[new Random().nextInt(quotes.length)];
        model.addAttribute("quote", quote);

        if (auth != null) {
            User user = userService.getCurrentUser(auth.getName());
            if (user != null) {
                model.addAttribute("totalHours", user.getTotalMinutes() / 60);
                model.addAttribute("totalMinutes", user.getTotalMinutes() % 60);
            }
        }
        return "index";
    }
}
