package com.intensivo.java.controller;

import com.intensivo.java.model.Conta;
import com.intensivo.java.model.ContaTipo;
import com.intensivo.java.model.ContaStatus;
import com.intensivo.java.model.Cliente;
import com.intensivo.java.service.ContaService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @GetMapping("/contas")
    public String listar(Model model) {
        model.addAttribute("contas", contaService.listarTodas());
        return "contas/list";
    }

    @GetMapping("/contas/novo")
    public String novaConta(Model model) {
        Conta conta = new Conta();
        conta.setTipo(ContaTipo.CORRENTE);
        conta.setStatus(ContaStatus.ATIVA);
        conta.setCliente(new Cliente());
        model.addAttribute("form", conta);
        model.addAttribute("modoEdicao", false);
        model.addAttribute("submitPath", "/contas");
        model.addAttribute("tituloFormulario", "Nova Conta");
        model.addAttribute("clientes", contaService.listarClientes());
        model.addAttribute("statusList", ContaStatus.values());
        model.addAttribute("tiposConta", ContaTipo.values());
        return "contas/form";
    }

    @PostMapping("/contas")
    public String criarConta(@ModelAttribute("form") Conta form,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            contaService.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Conta criada com sucesso.");
            return "redirect:/contas";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("modoEdicao", false);
            model.addAttribute("submitPath", "/contas");
            model.addAttribute("tituloFormulario", "Nova Conta");
            model.addAttribute("clientes", contaService.listarClientes());
            model.addAttribute("statusList", ContaStatus.values());
            model.addAttribute("tiposConta", ContaTipo.values());
            return "contas/form";
        }
    }

    @GetMapping("/contas/{id}/editar")
    public String editarConta(@PathVariable Long id, Model model) {
        Conta conta = contaService.buscar(id);
        model.addAttribute("form", conta);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("submitPath", "/contas/" + conta.getId());
        model.addAttribute("tituloFormulario", "Editar Conta");
        model.addAttribute("clientes", contaService.listarClientes());
        model.addAttribute("statusList", ContaStatus.values());
        model.addAttribute("tiposConta", ContaTipo.values());
        model.addAttribute("numeroConta", conta.getNumero());
        model.addAttribute("agenciaConta", conta.getAgencia());
        return "contas/form";
    }

    @PostMapping("/contas/{id}")
    public String atualizarConta(@PathVariable Long id,
            @ModelAttribute("form") Conta form,
            Model model,
            RedirectAttributes redirectAttributes) {
        form.setId(id);
        try {
            contaService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Conta atualizada com sucesso.");
            return "redirect:/contas";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("submitPath", "/contas/" + form.getId());
            model.addAttribute("tituloFormulario", "Editar Conta");
            model.addAttribute("clientes", contaService.listarClientes());
            model.addAttribute("statusList", ContaStatus.values());
            model.addAttribute("tiposConta", ContaTipo.values());
            return "contas/form";
        } catch (NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/contas";
        }
    }

    @PostMapping("/contas/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contaService.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Conta excluida com sucesso.");
        } catch (IllegalArgumentException | NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/contas";
    }
}
