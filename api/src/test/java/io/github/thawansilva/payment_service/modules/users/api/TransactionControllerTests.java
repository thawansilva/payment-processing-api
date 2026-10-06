package io.github.thawansilva.payment_service.modules.users.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.thawansilva.payment_service.modules.transactions.api.CreateTransactionRequestDto;
import io.github.thawansilva.payment_service.modules.transactions.api.TransactionController;
import io.github.thawansilva.payment_service.modules.transactions.internal.TransactionResponse;
import io.github.thawansilva.payment_service.modules.transactions.internal.TransactionService;
import io.github.thawansilva.payment_service.modules.transactions.internal.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTests {
    private MockMvc mockMvc;

    @Mock
    private AuthApi authApi;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        TransactionController controller = new TransactionController(authApi, transactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                User.withUsername("alice").password("").roles("USER").build(),
                null));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTransactionReturnsCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        TransactionResponse response = new TransactionResponse(
                transactionId,
                userId,
                new BigDecimal("100.00"),
                "USD",
                TransactionStatus.PENDING,
                Instant.now());

        when(authApi.getIdByUsername("alice")).thenReturn(userId);
        when(transactionService.createTransaction(eq(userId), eq("idempotency-key"), any(CreateTransactionRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/transactions")
                .header("Idempotency-Key", "idempotency-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":100.00,\"currency\":\"USD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(authApi).getIdByUsername("alice");
        verify(transactionService).createTransaction(eq(userId), eq("idempotency-key"), any(CreateTransactionRequestDto.class));
    }

    @Test
    void createTransactionRejectsMissingIdempotencyHeader() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":100.00,\"currency\":\"USD\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authApi);
        verifyNoInteractions(transactionService);
    }

    @Test
    void createTransactionRejectsInvalidAmount() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                .header("Idempotency-Key", "idempotency-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":0.00,\"currency\":\"USD\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authApi);
        verifyNoInteractions(transactionService);
    }
}
