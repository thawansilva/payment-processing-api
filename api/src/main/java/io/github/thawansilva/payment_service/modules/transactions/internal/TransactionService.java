package io.github.thawansilva.payment_service.modules.transactions.internal;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.thawansilva.payment_service.modules.audit.api.AuditApi;
import io.github.thawansilva.payment_service.modules.exception.api.TransactionNotFound;
import io.github.thawansilva.payment_service.modules.gateway.api.BankClientApi;
import io.github.thawansilva.payment_service.modules.transactions.api.CreateTransactionRequestDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	@Autowired
	final private TransactionRepository transactionRepo;
	@Autowired
	final private IdempotencyKeyRepository idempotencyKeyRepo;
	@Autowired
	final private BankClientApi bankClientApi;
	@Autowired
	final private AuditApi auditApi;

	final private Timer transactionTimer;

	public TransactionService(TransactionRepository transactionRepo,
			IdempotencyKeyRepository idempotencyKeyRepo,
			BankClientApi bankClientApi,
			AuditApi auditApi,
			MeterRegistry meterRegistry
		) {
		this.transactionRepo = transactionRepo;
		this.idempotencyKeyRepo = idempotencyKeyRepo;
		this.bankClientApi = bankClientApi;
		this.auditApi = auditApi;
		this.transactionTimer = Timer.
			builder("transactions.processing.time")
			.description("Time to process an transaction")
			.register(meterRegistry);
	}


	@Transactional
	public TransactionResponse createTransaction(UUID accountId, String key,
			CreateTransactionRequestDto newTransaction) {
		return transactionTimer.record(() -> {

			Optional<IdempotencyKey> found = idempotencyKeyRepo.findById(key);
			if (found.isPresent()) {
				UUID transactionId = found.get().getTransactionId();
				return getTransaction(accountId, transactionId, false);
			}

			Transaction transaction = new Transaction(accountId,
					newTransaction.amount(), newTransaction.currency());
			Transaction saved = transactionRepo.save(transaction);

			IdempotencyKey newKey = new IdempotencyKey(key, saved.getId());
			idempotencyKeyRepo.save(newKey);
			auditApi.record(accountId, saved.getId(), "PAYMENT_CREATED",
				"currency=" + saved.getCurrency() + " amount=" + saved.getAmount());
			String confirmation = bankClientApi.chargePayment(saved.getId());
			if (confirmation.startsWith("COMPLETED-")) {
				saved.setTransactionStatus(TransactionStatus.COMPLETED);
				saved = transactionRepo.save(saved);
				auditApi.record(accountId, saved.getId(),
					"PAYMENT_CONFIRMED", confirmation);
			}
			else {
				auditApi.record(accountId, saved.getId(),
					"PAYMENT_FAILED", confirmation);
			}
			return TransactionResponse.from(saved);
		});
	}

	public TransactionResponse getTransaction(UUID accountId, UUID transactionId,
			Boolean isAdmin) {
		Optional<Transaction> transaction;
		if (isAdmin) {
			transaction = transactionRepo.findById(transactionId);
		} else {
			transaction = transactionRepo
					.findByIdAndAccountId(transactionId, accountId);
		}
		transaction.orElseThrow(
				() -> new TransactionNotFound(transactionId.toString()));
		return TransactionResponse.from(transaction.get());
	}

	public Page<TransactionResponse> getAllTransactions(UUID accountId,
			Boolean isAdmin, Pageable pageable) {
		if (isAdmin) {
			return transactionRepo.findAll(pageable)
					.map(TransactionResponse::from);
		}
		return transactionRepo.findAllByAccountId(accountId, pageable)
				.map(TransactionResponse::from);
	}
}
