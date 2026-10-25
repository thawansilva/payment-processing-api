package io.github.thawansilva.payment_service.modules.audit.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thawansilva.payment_service.modules.audit.internal.AuditService;

@Component
public class AuditApi {
	@Autowired 
	private final AuditService auditService;

	public AuditApi(AuditService auditService) {
		this.auditService = auditService;
	}

	public void record(UUID actorId, UUID transactionId, String action,
		String details) {
		auditService.recordEvent(actorId, transactionId, action, details);
	}
}
