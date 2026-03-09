package com.intensivo.java.controller;

import com.intensivo.java.service.clientes.ClienteService;
import com.intensivo.java.service.contas.ContaService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ClienteService clienteService;
    private final ContaService contaService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        model.addAttribute("totalClientes", clienteService.contarClientes());
        model.addAttribute("totalContas", contaService.contarContas());
        model.addAttribute("usuarioLogado", principal.getName());
        return "index";
    }
}
