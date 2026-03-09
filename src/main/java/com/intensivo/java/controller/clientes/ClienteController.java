package com.intensivo.java.controller.clientes;

import com.intensivo.java.dto.form.clientes.ClienteForm;
import com.intensivo.java.exception.BusinessException;
import com.intensivo.java.model.clientes.Cliente;
import com.intensivo.java.model.clientes.TipoCliente;
import com.intensivo.java.service.clientes.ClienteService;
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

    @GetMapping("/clientes/novo")
    public String novo(Model model) {
        prepararFormulario(model, new ClienteForm(), false);
        return "clientes/form";
    }

    @PostMapping("/clientes")
    public String criar(@Valid @ModelAttribute("form") ClienteForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, form, false);
            return "clientes/form";
        }

        try {
            clienteService.criar(form);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente cadastrado com sucesso.");
            return "redirect:/clientes";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormulario(model, form, false);
            return "clientes/form";
        }
    }

    @GetMapping("/clientes/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscar(id);
        prepararFormulario(model, toForm(cliente), true);
        return "clientes/form";
    }

    @PostMapping("/clientes/{id}")
    public String atualizar(@PathVariable Long id,
            @Valid @ModelAttribute("form") ClienteForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepararFormulario(model, form, true);
            return "clientes/form";
        }

        try {
            clienteService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente atualizado com sucesso.");
            return "redirect:/clientes";
        } catch (BusinessException exception) {
            bindingResult.reject("business", exception.getMessage());
            prepararFormulario(model, form, true);
            return "clientes/form";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/clientes/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente excluido com sucesso.");
        return "redirect:/clientes";
    }

    private void prepararFormulario(Model model, ClienteForm form, boolean edicao) {
        model.addAttribute("form", form);
        model.addAttribute("modoEdicao", edicao);
        model.addAttribute("submitPath", edicao ? "/clientes/" + form.getId() : "/clientes");
        model.addAttribute("tituloFormulario", edicao ? "Editar Cliente" : "Novo Cliente");
        model.addAttribute("tipoClientes", TipoCliente.values());
    }

    private ClienteForm toForm(Cliente cliente) {
        ClienteForm form = new ClienteForm();
        form.setId(cliente.getId());
        form.setTipoCliente(cliente.getTipoCliente());
        form.setNome(cliente.getNome());
        form.setDocumento(cliente.getDocumento());
        preencherCamposComuns(form, cliente);
        return form;
    }

    private void preencherCamposComuns(ClienteForm form, Cliente cliente) {
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
