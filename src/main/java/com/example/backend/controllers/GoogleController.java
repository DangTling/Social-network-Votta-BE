package com.example.backend.controllers;

import com.example.backend.helpers.LoginHelper;
import com.example.backend.models.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.utils.AuthenticationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
public class GoogleController {
    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private AuthenticationUtil authenticationUtil;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/google/callback")
    public void callback(@RequestParam String code, @RequestParam String scope, HttpServletResponse response, HttpServletRequest request) throws IOException {
        String cookieToken = authenticationUtil.getTokenFromCookie(request);
        String frontEndUrl = "http://localhost:5173/";
        if (cookieToken == null) {
            String accessToken = loginHelper.getAccessTokenGoogle(code, scope);
            User user = (User) loginHelper.loginGoogle(accessToken).getBody();
            String token = authenticationUtil.generateToken(user.getId());
            authenticationUtil.createCookie(response, token);
        }
        response.sendRedirect(frontEndUrl);
    }
}
