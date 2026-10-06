package io.github.thawansilva.payment_service.modules.external;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class BankGatewayClient {
	final private Logger logger = org.slf4j.LoggerFactory.getLogger(BankGatewayClient.class);

	public String charge(UUID transactionId) {
		logger.info("Charging transaction " + transactionId);

		if (ThreadLocalRandom.current().nextInt(100) < 40) { // 40% of chance of failing
			logger.info("Bank gateway timeout");
			throw new BankGatewayException("Bank gateway timeout on transaction: " + transactionId);
		}
		return "COMPLETED-" + transactionId;
	}
}
