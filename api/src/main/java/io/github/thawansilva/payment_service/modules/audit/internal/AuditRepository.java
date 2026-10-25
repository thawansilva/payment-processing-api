package io.github.thawansilva.payment_service.modules.audit.internal;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface AuditRepository extends JpaRepository<Audit, UUID> { }