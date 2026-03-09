package com.intensivo.java.exception;

import com.intensivo.java.controller.HomeController;
import com.intensivo.java.controller.clientes.ClienteController;
import com.intensivo.java.controller.contas.ContaController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@ControllerAdvice(assignableTypes = {HomeController.class, ClienteController.class, ContaController.class})
public class MvcExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException exception,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        return "redirect:" + fallbackPath(request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpected(Exception exception) {
        log.error("Erro inesperado na camada MVC", exception);
        return new ModelAndView("error/500");
    }

    private String fallbackPath(String uri) {
        if (uri.startsWith("/contas")) {
            return "/contas";
        }
        if (uri.startsWith("/clientes")) {
            return "/clientes";
        }
        return "/";
    }
}
