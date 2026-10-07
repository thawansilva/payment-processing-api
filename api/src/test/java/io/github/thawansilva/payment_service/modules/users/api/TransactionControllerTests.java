package io.github.thawansilva.payment_service.modules.users.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.ServletException;
import io.github.thawansilva.payment_service.modules.gateway.api.BankClientApi;
import io.github.thawansilva.payment_service.modules.transactions.api.CreateTransactionRequestDto;
import io.github.thawansilva.payment_service.modules.transactions.api.TransactionController;
import io.github.thawansilva.payment_service.modules.transactions.internal.IdempotencyKeyRepository;
import io.github.thawansilva.payment_service.modules.transactions.internal.Transaction;
import io.github.thawansilva.payment_service.modules.transactions.internal.TransactionRepository;
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

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private BankClientApi bankClientApi;

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

    @Test
    void userCanGetOwnTransaction() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        when(authApi.getIdByUsername("alice")).thenReturn(userId);
        when(transactionRepository.findByIdAndAccountId(transactionId, userId))
                .thenReturn(Optional.of(new Transaction(userId, new BigDecimal("100.00"), "USD")));
        mockMvc = mockMvcWithRealTransactionService();

        mockMvc.perform(get("/api/v1/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.amount").value(100.00));

        verify(transactionRepository).findByIdAndAccountId(transactionId, userId);
        verify(transactionRepository, never()).findById(transactionId);
    }

    @Test
    void userCannotGetAnotherUsersTransaction() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        when(authApi.getIdByUsername("alice")).thenReturn(userId);
        when(transactionRepository.findByIdAndAccountId(transactionId, userId)).thenReturn(Optional.empty());
        mockMvc = mockMvcWithRealTransactionService();

        assertThrows(ServletException.class,
                () -> mockMvc.perform(get("/api/v1/transactions/{id}", transactionId)));

        verify(transactionRepository).findByIdAndAccountId(transactionId, userId);
        verify(transactionRepository, never()).findById(transactionId);
    }

    @Test
    void adminCanGetAnotherUsersTransaction() throws Exception {
        UUID adminId = UUID.randomUUID();
        UUID transactionOwnerId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                User.withUsername("admin").password("").roles("ADMIN").build(),
                null));
        when(authApi.getIdByUsername("admin")).thenReturn(adminId);
        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.of(new Transaction(transactionOwnerId, new BigDecimal("100.00"), "USD")));
        mockMvc = mockMvcWithRealTransactionService();

        mockMvc.perform(get("/api/v1/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(transactionOwnerId.toString()))
                .andExpect(jsonPath("$.amount").value(100.00));

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository, never()).findByIdAndAccountId(transactionId, adminId);
    }

    private MockMvc mockMvcWithRealTransactionService() {
        TransactionService realTransactionService = new TransactionService(
                transactionRepository, idempotencyKeyRepository, bankClientApi);
        TransactionController controller = new TransactionController(authApi, realTransactionService);
        return MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }
}
