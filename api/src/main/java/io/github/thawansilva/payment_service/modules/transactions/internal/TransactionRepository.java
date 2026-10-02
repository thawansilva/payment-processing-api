package io.github.thawansilva.payment_service.modules.transactions.internal;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
	Page<Transaction> findAllByAccountId(UUID accountId, Pageable pageable);
	Optional<Transaction> findByIdAndAccountId(UUID id, UUID accountId);
}
