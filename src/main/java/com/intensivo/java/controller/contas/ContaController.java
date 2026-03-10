package com.intensivo.java.controller.contas;

import com.intensivo.java.dto.form.contas.ContaCorrenteForm;
import com.intensivo.java.dto.form.contas.ContaJuridicaForm;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContaController {

    @GetMapping("/contas")
    public String listar(Model model) {
        model.addAttribute("contas", List.of());
        return "contas/list";
    }

    @GetMapping("/contas/corrente/novo")
    public String novaContaCorrente(Model model) {
        model.addAttribute("form", new ContaCorrenteForm());
        model.addAttribute("tituloFormulario", "Nova Conta Corrente");
        model.addAttribute("clientes", List.of());
        model.addAttribute("statusList", List.of("ATIVA", "BLOQUEADA"));
        return "contas/corrente-form";
    }

    @GetMapping("/contas/juridica/novo")
    public String novaContaJuridica(Model model) {
        model.addAttribute("form", new ContaJuridicaForm());
        model.addAttribute("tituloFormulario", "Nova Conta Juridica");
        model.addAttribute("clientes", List.of());
        model.addAttribute("statusList", List.of("ATIVA", "BLOQUEADA"));
        return "contas/juridica-form";
    }
}
