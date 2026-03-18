package com.cfr.networkapp.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CsrfController {
    @GetMapping("/api/csrf")
    public Map<String, String> csrf(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("org.springframework.security.web.csrf.CsrfToken");
        Map<String, String> response = new HashMap<>();
        if (token != null) {
            response.put("token", token.getToken());
            response.put("headerName", token.getHeaderName());
            response.put("parameterName", token.getParameterName());
        }
        return response;
    }
}
