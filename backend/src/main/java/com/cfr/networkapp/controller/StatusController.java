package com.cfr.networkapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @GetMapping
    public Map<String, String> getStatus() {
        return Map.of("status", "UP", "message", "Backend is running and healthy");
    }
}