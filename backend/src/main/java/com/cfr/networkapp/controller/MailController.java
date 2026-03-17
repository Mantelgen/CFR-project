package com.cfr.networkapp.controller;

import com.cfr.networkapp.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @GetMapping("/test")
    public String sendTestEmail() {
        mailService.sendTestEmail("test@cfr.local");
        return "Email sent!";
    }
}
