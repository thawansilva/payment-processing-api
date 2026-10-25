package io.github.thawansilva.payment_service.modules.audit.internal;

import java.util.UUID;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
	@Autowired
	private final AuditRepository auditRepository;

	private final Logger logger = org.slf4j.LoggerFactory
		.getLogger(LoggerFactory.class);

	public AuditService(AuditRepository auditRepository) {
		this.auditRepository = auditRepository;
	}

	public void recordEvent(UUID actorId, UUID transactionId, String action,
		String details) {
		auditRepository.save(new Audit(actorId, transactionId, action, details));
		logger.info("AUDIT {} transaction={} actor={} {}", action,
			transactionId, actorId, details);
	}
}
