package com.example.Ejemplo.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        // Log detallado del error
        log.error("Error detectado - Status: {}, URI: {}, Message: {}, Exception: {}", 
                 status, requestUri, message, exception);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            // Agregar información al modelo para debugging
            model.addAttribute("statusCode", statusCode);
            model.addAttribute("requestUri", requestUri);
            model.addAttribute("errorMessage", message);
            
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            }
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                log.warn("404 - Página no encontrada: {}", requestUri);
                model.addAttribute("mensaje", "La página que buscas no existe");
                return "error/error";
            }
            
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                log.error("500 - Error interno del servidor en: {}", requestUri);
                if (exception != null) {
                    log.error("Excepción: ", (Exception) exception);
                }
                model.addAttribute("mensaje", "Error interno del servidor");
                return "error/error";
            }
        }
        
        return "error/error";
    }
}
