package com.cfr.networkapp.controller;

import jakarta.servlet.http.HttpServletResponse;
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
    public Map<String, String> getInfo(HttpServletResponse response) throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();

        // Prevent caching so each refresh asks the load balancer for a fresh backend response.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> info = new LinkedHashMap<>();
        info.put("serverHost", localHost.getHostName());
        info.put("serverIp", localHost.getHostAddress());
        info.put("servedAt", Instant.now().toString());
        return info;
    }
}
