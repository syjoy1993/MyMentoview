package ce2team1.mentoview.payment.application.service;

import ce2team1.mentoview.payment.application.dto.PortonePayment;
import ce2team1.mentoview.subscription.application.dto.SubscriptionDto;

import java.math.BigDecimal;

public interface PaymentVerifier {
    void verifyPayment(PortonePayment portonePayment, SubscriptionDto subscription, BigDecimal expectedAmount);
}
