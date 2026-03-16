package com.intensivo.java.controller;

import com.intensivo.java.model.Cliente;
import com.intensivo.java.model.Endereco;
import com.intensivo.java.model.TipoCliente;
import com.intensivo.java.service.ClienteService;
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
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/clientes")
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes/list";
    }

    @GetMapping("/clientes/novo")
    public String novo(Model model) {
        Cliente cliente = new Cliente();
        cliente.setEndereco(new Endereco());
        model.addAttribute("form", cliente);
        model.addAttribute("modoEdicao", false);
        model.addAttribute("submitPath", "/clientes");
        model.addAttribute("tituloFormulario", "Novo Cliente");
        model.addAttribute("tipoClientes", TipoCliente.values());       
        return "clientes/form";
    }

    @PostMapping("/clientes")
    public String criar(@ModelAttribute("form") Cliente form,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            clienteService.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente cadastrado com sucesso.");
            return "redirect:/clientes";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("modoEdicao", false);
            model.addAttribute("submitPath", "/clientes");
            model.addAttribute("tituloFormulario", "Novo Cliente");
            model.addAttribute("tipoClientes", TipoCliente.values());
            return "clientes/form";
        }
    }

    @GetMapping("/clientes/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscar(id);
        if (cliente.getEndereco() == null) {
            cliente.setEndereco(new Endereco());
        }
        model.addAttribute("form", cliente);
        model.addAttribute("modoEdicao", true);
        model.addAttribute("submitPath", "/clientes/" + cliente.getId());
        model.addAttribute("tituloFormulario", "Editar Cliente");
        model.addAttribute("tipoClientes", TipoCliente.values());
        return "clientes/form";
    }

    @PostMapping("/clientes/{id}")
    public String atualizar(@PathVariable Long id,
            @ModelAttribute("form") Cliente form,
            Model model,
            RedirectAttributes redirectAttributes) {
        form.setId(id);
        try {
            clienteService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente atualizado com sucesso.");
            return "redirect:/clientes";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("submitPath", "/clientes/" + form.getId());
            model.addAttribute("tituloFormulario", "Editar Cliente");
            model.addAttribute("tipoClientes", TipoCliente.values());
            return "clientes/form";
        } catch (NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/clientes/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.excluir(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente excluido com sucesso.");
        } catch (IllegalArgumentException | NoSuchElementException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/clientes";
    }

}
