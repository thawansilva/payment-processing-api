package io.github.thawansilva.payment_service.modules.users.api;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.thawansilva.payment_service.modules.users.internal.AuthService;
import io.github.thawansilva.payment_service.modules.users.internal.JwtService;
import io.github.thawansilva.payment_service.modules.users.internal.RefreshToken;
import io.github.thawansilva.payment_service.modules.users.internal.RefreshTokenService;
import io.github.thawansilva.payment_service.modules.users.internal.User;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authService, authManager, jwtService, refreshTokenService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void registerReturnsSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered\n"));

        verify(authService).register(any(RequestRegisterDto.class));
    }

    @Test
    void registerReturnsFailedOnShortPassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\",\"password\":\"12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerAdminWithTrailingSlashReturnsSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register/admin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"adminuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin user created successfully\n"));

        verify(authService).registerAdmin(any(RequestRegisterDto.class));
    }

    @Test
    void loginReturnsAccessAndRefreshTokens() throws Exception {
        UUID userId = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken(
                "refresh-token",
                userId,
                Instant.now().plusSeconds(3600));

        when(authManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user1");
        when(jwtService.generateToken(userDetails)).thenReturn("access-token");
        when(authService.getIdByUsername("user1")).thenReturn(userId);
        when(refreshTokenService.generateRefreshToken(userId)).thenReturn(refreshToken);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user1\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void refreshTokenShouldReturnNewTokens() throws Exception {
        UUID userId = UUID.randomUUID();
        RefreshToken currentRefreshToken = new RefreshToken(
                "refresh-token",
                userId,
                Instant.now().plusSeconds(240));
        RefreshToken newRefreshToken = new RefreshToken(
                "new-refresh-token",
                userId,
                Instant.now().plusSeconds(3600));
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getUsername()).thenReturn("user1");
        when(user.getPasswordHash()).thenReturn("password-hash");
        when(user.getRole()).thenReturn("user");

        when(refreshTokenService.verifyAndGetToken("refresh-token")).thenReturn(currentRefreshToken);
        when(authService.getUserById(userId)).thenReturn(user);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("access-token");
        when(refreshTokenService.generateRefreshToken(userId)).thenReturn(newRefreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));

        verify(refreshTokenService).verifyAndGetToken("refresh-token");
        verify(authService).getUserById(userId);
        verify(refreshTokenService).revokeById(currentRefreshToken.getId());
        verify(refreshTokenService).generateRefreshToken(userId);
    }

    @Test
    void refreshTokenShouldRejectRevokedToken() {
        when(refreshTokenService.verifyAndGetToken("revoked-token"))
                .thenThrow(new IllegalArgumentException("Refresh token revoked"));

        assertThrows(ServletException.class, () -> mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"revoked-token\"}")));
    }

    @Test
    void refreshTokenShouldRejectUnknownToken() {
        when(refreshTokenService.verifyAndGetToken("unknown-token"))
                .thenThrow(new IllegalArgumentException("Refresh token not found"));

        assertThrows(ServletException.class, () -> mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"unknown-token\"}")));
    }
}