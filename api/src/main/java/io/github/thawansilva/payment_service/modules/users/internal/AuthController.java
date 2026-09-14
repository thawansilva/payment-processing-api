package io.github.thawansilva.payment_service.modules.users.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    final private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RequestRegisterDto user) {
        try {
            authService.register(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return  ResponseEntity.ok("User registered");
    }

    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@RequestBody RequestRegisterDto user) {
        return  ResponseEntity.status(HttpStatus.OK).body("Created");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody RequestRegisterDto user) {
        return  ResponseEntity.status(HttpStatus.CREATED).body("Created");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody RequestRegisterDto user) {
        return  ResponseEntity.status(HttpStatus.OK).body("Created");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RequestRegisterDto user) {
        return  ResponseEntity.status(HttpStatus.OK).body("Created");
    }
}
