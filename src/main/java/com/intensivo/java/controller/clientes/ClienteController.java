package com.intensivo.java.controller.clientes;

import com.intensivo.java.dto.ClienteDto;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

    @PostMapping({"/clientes", "/clientes/novo"})
    public String salvar(@ModelAttribute ClienteDto clienteDto, Model model) {
        System.out.println("Cliente recebido no formulario: " + clienteDto);

        model.addAttribute("tituloFormulario", "Novo Cliente");
        model.addAttribute("tipoClientes", List.of("Pessoa Fisica", "Pessoa Juridica"));
        model.addAttribute("mensagemSucesso", "Cliente recebido com sucesso no backend.");
        return "clientes/form";
    }
}
