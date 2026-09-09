package io.github.thawansilva.payment_service.modules.users.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
    final private AuthService authentication;

    @Autowired
    public AuthController(AuthService authentication) {
        this.authentication = authentication;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RequestRegisterDto user) {
        return  ResponseEntity.status(HttpStatus.CREATED).body("Created");
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
