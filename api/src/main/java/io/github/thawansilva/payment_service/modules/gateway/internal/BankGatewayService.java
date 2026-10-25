package io.github.thawansilva.payment_service.modules.gateway.internal;

import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.thawansilva.payment_service.modules.external.BankGatewayClient;

@Service
public class BankGatewayService {
	@Autowired
	final private BankGatewayClient bankGatewayClient;
	final private Logger logger = org.slf4j.LoggerFactory.getLogger(BankGatewayClient.class);

	public BankGatewayService(BankGatewayClient bankGatewayClient) {
		this.bankGatewayClient = bankGatewayClient;
	}

	@CircuitBreaker(name = "bankGateway", fallbackMethod = "fallback")
	@Retry(name = "bankGateway", fallbackMethod = "fallback")
	public String chargePayment(UUID transactionId) {
		return bankGatewayClient.charge(transactionId);
	}

	private String fallback(UUID transactionId, Throwable t) {
		logger.warn("Bank gateway unavailable for transaction {}, reason: {}",
			transactionId, t.getMessage());
		return "PENDING-MANUAL-CHECK-" + transactionId;
	}
}
