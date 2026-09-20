package io.github.thawansilva.payment_service.modules.users.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.thawansilva.payment_service.modules.users.internal.AuthService;
import io.github.thawansilva.payment_service.modules.users.internal.JwtService;
import io.github.thawansilva.payment_service.modules.users.internal.LoginResponse;
import io.github.thawansilva.payment_service.modules.users.internal.RefreshToken;
import io.github.thawansilva.payment_service.modules.users.internal.RefreshTokenService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    final private AuthService authService;
    @Autowired
    final private AuthenticationManager authManager;
    @Autowired
    final private JwtService jwtService;
    @Autowired
    final private RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, 
        AuthenticationManager authManager,
        JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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

    @PostMapping({"/register/admin", "/register/admin/"})
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RequestRegisterDto user) {
        try {
            authService.registerAdmin(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.ok("Admin user created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequestDto loginReq) {
        Authentication authentication = authManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginReq.username(), loginReq.password()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        UUID userId = authService.getIdByUsername(userDetails.getUsername());
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(userId);

        return  ResponseEntity.ok(new LoginResponse(accessToken, refreshToken.getToken()));
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
