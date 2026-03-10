package com.intensivo.java.controller.clientes;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

    @GetMapping("/clientes")
    public String listar(Model model) {
        model.addAttribute("clientes", List.of());
        return "clientes/list";
    }

    @GetMapping("/clientes/novo")
    public String novo(Model model) {
        model.addAttribute("tituloFormulario", "Novo Cliente");
        model.addAttribute("tipoClientes", List.of("Pessoa Fisica", "Pessoa Juridica"));
        return "clientes/form";
    }
}
