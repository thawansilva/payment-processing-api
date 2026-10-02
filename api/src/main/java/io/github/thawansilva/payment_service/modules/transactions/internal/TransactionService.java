package io.github.thawansilva.payment_service.modules.transactions.internal;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.thawansilva.payment_service.modules.transactions.api.CreateTransactionRequestDto;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	@Autowired
	final private TransactionRepository transactionRepo;
	@Autowired
	final private IdempotencyKeyRepository idempotencyKeyRepo;

	public TransactionService(TransactionRepository transactionRepo,
		IdempotencyKeyRepository idempotencyKeyRepo
	) {
		this.transactionRepo = transactionRepo;
		this.idempotencyKeyRepo = idempotencyKeyRepo;
	}

	@Transactional
	public TransactionResponse createTransaction(UUID accountId, String key,
			CreateTransactionRequestDto newTransaction) {
		Optional<IdempotencyKey> found = idempotencyKeyRepo.findById(key);
		if (found.isPresent()) {
			UUID transactionId = found.get().getTransactionId();
			System.out.println("Transaction was found, id: " + transactionId);
			return getTransaction(key, transactionId, false);
		}

		Transaction transaction = new Transaction(accountId,
		newTransaction.amount(), newTransaction.currency());
		Transaction saved = transactionRepo.save(transaction);

		IdempotencyKey newKey = new IdempotencyKey(key, saved.getId());
		idempotencyKeyRepo.save(newKey);
		return TransactionResponse.from(saved);
	}

		public TransactionResponse getTransaction(String key, UUID transactionId,
		Boolean isAdmin) {
		Optional<Transaction> transaction;
		if (isAdmin) {
			transaction = transactionRepo
				.findById(transactionId);
		}
		else {
			transaction = transactionRepo
				.findByIdAndAccountId(transactionId, transactionId);
		}
		transaction.orElseThrow(
			() -> new NoSuchElementException("Transaction not found: " + transactionId));
		return TransactionResponse.from(transaction.get());
	}
}
