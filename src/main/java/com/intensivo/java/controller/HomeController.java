package com.intensivo.java.controller;

import com.intensivo.java.service.ClienteService;
import com.intensivo.java.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ClienteService clienteService;
    private final ContaService contaService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalClientes", clienteService.contarClientes());
        model.addAttribute("totalContas", contaService.contarContas());
        return "index";
    }
}
