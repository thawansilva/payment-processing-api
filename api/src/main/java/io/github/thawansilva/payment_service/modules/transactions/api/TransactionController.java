package io.github.thawansilva.payment_service.modules.transactions.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.thawansilva.payment_service.modules.transactions.internal.TransactionResponse;
import io.github.thawansilva.payment_service.modules.transactions.internal.TransactionService;
import io.github.thawansilva.payment_service.modules.users.api.AuthApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions operations")
public class TransactionController {
	@Autowired 
	final private TransactionService transactionService;
	@Autowired 
	final private AuthApi authApi;

	public TransactionController(AuthApi authApi,
			TransactionService transactionService) {
		this.authApi = authApi;
		this.transactionService = transactionService;
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	@Operation(description="Create transaction with idempotency key")
	public ResponseEntity<TransactionResponse> createTransaction(@Valid
			@RequestBody CreateTransactionRequestDto transaction,
			@RequestHeader(value = "Idempotency-Key", required=true) String key,
			@AuthenticationPrincipal UserDetails userDetails) {
		UUID userId = authApi.getIdByUsername(userDetails.getUsername());
		TransactionResponse response = transactionService
			.createTransaction(userId, key, transaction);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<TransactionResponse> getTransaction(
			@PathVariable UUID id,
			@AuthenticationPrincipal UserDetails userDetails) {
		UUID userId = authApi.getIdByUsername(userDetails.getUsername());
		boolean isAdmin = userDetails
			.getAuthorities()
			.stream()
			.anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
			TransactionResponse response = transactionService
				.getTransaction(userId, id, isAdmin);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping()
	public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
			@PageableDefault(sort="createdAt", direction= Sort.Direction.DESC)
			Pageable pageable,
			@AuthenticationPrincipal UserDetails userDetails) {
		UUID userId = authApi.getIdByUsername(userDetails.getUsername());
		boolean isAdmin = userDetails
			.getAuthorities()
			.stream()
			.anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
		return ResponseEntity
			.ok(transactionService.getAllTransactions(userId, isAdmin, pageable));
	}
}
