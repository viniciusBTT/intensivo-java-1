package com.intensivo.java.controller;

import com.intensivo.java.model.ClientePessoaFisica;
import com.intensivo.java.model.ClientePessoaJuridica;
import com.intensivo.java.dto.form.ClientePessoaFisicaForm;
import com.intensivo.java.dto.form.ClientePessoaJuridicaForm;
import com.intensivo.java.exception.BusinessException;
import com.intensivo.java.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/clientes/pf/novo")
    public String novoPf(Model model) {
        prepararFormularioPf(model, new ClientePessoaFisicaForm(), false);
        return "clientes/pf-form";
    }

    @PostMapping("/clientes/pf")
    public String criarPf(@Valid @ModelAttribute("form") ClientePessoaFisicaForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormularioPf(model, form, false);
            return "clientes/pf-form";
        }

        try {
            clienteService.criarPessoaFisica(form);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente PF cadastrado com sucesso.");
            return "redirect:/clientes";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormularioPf(model, form, false);
            return "clientes/pf-form";
        }
    }

    @GetMapping("/clientes/pf/{id}/editar")
    public String editarPf(@PathVariable Long id, Model model) {
        ClientePessoaFisica cliente = clienteService.buscarPessoaFisica(id);
        prepararFormularioPf(model, toPfForm(cliente), true);
        return "clientes/pf-form";
    }

    @PostMapping("/clientes/pf/{id}")
    public String atualizarPf(@PathVariable Long id,
            @Valid @ModelAttribute("form") ClientePessoaFisicaForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormularioPf(model, form, true);
            return "clientes/pf-form";
        }

        try {
            clienteService.atualizarPessoaFisica(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente PF atualizado com sucesso.");
            return "redirect:/clientes";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormularioPf(model, form, true);
            return "clientes/pf-form";
        }
    }

    @GetMapping("/clientes/pj/novo")
    public String novoPj(Model model) {
        prepararFormularioPj(model, new ClientePessoaJuridicaForm(), false);
        return "clientes/pj-form";
    }

    @PostMapping("/clientes/pj")
    public String criarPj(@Valid @ModelAttribute("form") ClientePessoaJuridicaForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormularioPj(model, form, false);
            return "clientes/pj-form";
        }

        try {
            clienteService.criarPessoaJuridica(form);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente PJ cadastrado com sucesso.");
            return "redirect:/clientes";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormularioPj(model, form, false);
            return "clientes/pj-form";
        }
    }

    @GetMapping("/clientes/pj/{id}/editar")
    public String editarPj(@PathVariable Long id, Model model) {
        ClientePessoaJuridica cliente = clienteService.buscarPessoaJuridica(id);
        prepararFormularioPj(model, toPjForm(cliente), true);
        return "clientes/pj-form";
    }

    @PostMapping("/clientes/pj/{id}")
    public String atualizarPj(@PathVariable Long id,
            @Valid @ModelAttribute("form") ClientePessoaJuridicaForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormularioPj(model, form, true);
            return "clientes/pj-form";
        }

        try {
            clienteService.atualizarPessoaJuridica(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente PJ atualizado com sucesso.");
            return "redirect:/clientes";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormularioPj(model, form, true);
            return "clientes/pj-form";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/clientes/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente excluido com sucesso.");
        return "redirect:/clientes";
    }

    private void prepararFormularioPf(Model model, ClientePessoaFisicaForm form, boolean edicao) {
        model.addAttribute("form", form);
        model.addAttribute("modoEdicao", edicao);
        model.addAttribute("submitPath", edicao ? "/clientes/pf/" + form.getId() : "/clientes/pf");
        model.addAttribute("tituloFormulario", edicao ? "Editar Cliente PF" : "Novo Cliente PF");
    }

    private void prepararFormularioPj(Model model, ClientePessoaJuridicaForm form, boolean edicao) {
        model.addAttribute("form", form);
        model.addAttribute("modoEdicao", edicao);
        model.addAttribute("submitPath", edicao ? "/clientes/pj/" + form.getId() : "/clientes/pj");
        model.addAttribute("tituloFormulario", edicao ? "Editar Cliente PJ" : "Novo Cliente PJ");
    }

    private ClientePessoaFisicaForm toPfForm(ClientePessoaFisica cliente) {
        ClientePessoaFisicaForm form = new ClientePessoaFisicaForm();
        form.setId(cliente.getId());
        form.setNomeCompleto(cliente.getNomeCompleto());
        form.setCpf(cliente.getCpf());
        preencherCamposComuns(form, cliente);
        return form;
    }

    private ClientePessoaJuridicaForm toPjForm(ClientePessoaJuridica cliente) {
        ClientePessoaJuridicaForm form = new ClientePessoaJuridicaForm();
        form.setId(cliente.getId());
        form.setRazaoSocial(cliente.getRazaoSocial());
        form.setNomeFantasia(cliente.getNomeFantasia());
        form.setCnpj(cliente.getCnpj());
        preencherCamposComuns(form, cliente);
        return form;
    }

    private void preencherCamposComuns(com.intensivo.java.dto.form.ClienteForm form,
            com.intensivo.java.model.Cliente cliente) {
        form.setEmail(cliente.getEmail());
        form.setTelefone(cliente.getTelefone());
        form.setCep(cliente.getEndereco().getCep());
        form.setNumero(cliente.getEndereco().getNumero());
        form.setComplemento(cliente.getEndereco().getComplemento());
        form.setLogradouro(cliente.getEndereco().getLogradouro());
        form.setBairro(cliente.getEndereco().getBairro());
        form.setCidade(cliente.getEndereco().getCidade());
        form.setUf(cliente.getEndereco().getUf());
    }
}
