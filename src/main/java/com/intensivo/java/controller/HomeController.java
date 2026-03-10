package com.intensivo.java.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalClientes", 0);
        model.addAttribute("totalContas", 0);
        return "index";
    }
}
