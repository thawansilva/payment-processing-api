package io.github.thawansilva.payment_service.modules.users.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto user) {
        try {
            authService.register(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.ok("User registered\n");
    }

    @PostMapping({ "/register/admin", "/register/admin/" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegisterRequestDto user) {
        try {
            authService.registerAdmin(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.ok("Admin user created successfully\n");
    }

    @RateLimiter(name = "login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequestDto loginReq) {
        Authentication authentication = authManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginReq.username(), loginReq.password()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        UUID userId = authService.getIdByUsername(userDetails.getUsername());
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(userId);

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequestDto refreshRequest) {
        RefreshToken refToken = refreshTokenService.verifyAndGetToken(refreshRequest.refreshToken());
        var user = authService.getUserById(refToken.getUserId());
        refreshTokenService.revokeById(refToken.getId());
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(user.getRolesList()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList())
            .build();

        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenService
            .generateRefreshToken(user.getId());
        return  ResponseEntity
            .ok(new LoginResponse(accessToken, newRefreshToken.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = authService.getIdByUsername(userDetails.getUsername());
        refreshTokenService.revokeAllByUserId(userId);
        return  ResponseEntity.status(HttpStatus.OK).body("User logged out\n");
    }
}
