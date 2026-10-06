package io.github.thawansilva.payment_service.modules.gateway.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thawansilva.payment_service.modules.gateway.internal.BankGatewayService;

@Component
public class BankClientApi {
	@Autowired
	final private BankGatewayService bankGatewayService;

	public BankClientApi(BankGatewayService bankGatewayService) {
		this.bankGatewayService = bankGatewayService;
	}

	public String chargePayment(UUID transactionId) {
		return bankGatewayService.chargePayment(transactionId);
	}
}
