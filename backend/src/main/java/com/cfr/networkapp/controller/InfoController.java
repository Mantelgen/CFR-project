package com.cfr.networkapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class InfoController {
    @GetMapping("/api/info")
    public String getInfo() throws UnknownHostException {
        return "Server IP: " + InetAddress.getLocalHost().getHostAddress();
    }
}
