package ce2team1.mentoview.payment.application;

import ce2team1.mentoview.service.dto.PortonePayment;
import ce2team1.mentoview.service.dto.SubscriptionDto;

import java.math.BigDecimal;

public interface PaymentVerifier {
    void verifyPayment(PortonePayment portonePayment, SubscriptionDto subscription, BigDecimal expectedAmount);
}
