package com.cfr.networkapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class InfoController {
    @GetMapping("/api/info")
    public Map<String, String> getInfo(HttpServletRequest request, HttpServletResponse response) throws UnknownHostException {
        // Get the real network IP of the backend container (not localhost)
        String backendIp = null;
        String backendHost = null;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            backendIp = ip.getHostAddress();
            backendHost = ip.getHostName();
        } catch (Exception e) {
            backendIp = "unknown-ip";
            backendHost = "unknown-host";
        }

        // Get the client IP as seen by the backend (from X-Real-IP or X-Forwarded-For)
        String clientIp = request.getHeader("X-Real-IP");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp != null && clientIp.contains(",")) {
                clientIp = clientIp.split(",")[0].trim();
            }
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        // Prevent caching so each refresh asks the load balancer for a fresh backend response.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> info = new LinkedHashMap<>();
        info.put("serverHost", backendHost);
        info.put("serverIp", backendIp);
        info.put("clientIp", clientIp);
        info.put("servedAt", Instant.now().toString());
        return info;
    }
}
