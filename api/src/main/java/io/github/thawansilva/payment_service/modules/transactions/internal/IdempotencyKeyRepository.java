package io.github.thawansilva.payment_service.modules.transactions.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String>{ }
