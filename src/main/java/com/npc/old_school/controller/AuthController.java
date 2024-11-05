package com.npc.old_school.controller;

import com.npc.old_school.dto.auth.LoginDTO;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResultResponse login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResultResponse.success(authService.login(loginDTO));
    }
} 