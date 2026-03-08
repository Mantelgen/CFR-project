package com.cfr.networkapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class StatusController {

    @GetMapping("/api/status")
    public String getStatus() throws UnknownHostException {
        // Requirement: Web server displaying its IP address
        return "Backend is running! Server IP: " + InetAddress.getLocalHost().getHostAddress();
    }
}