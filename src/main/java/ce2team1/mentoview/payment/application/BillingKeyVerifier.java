package ce2team1.mentoview.payment.application;

import ce2team1.mentoview.payment.infra.portone.dto.PortoneBillingKey;
import ce2team1.mentoview.service.dto.SubscriptionDto;
/*
* 외부API를 통해 받은 BillingKey 와 Subscription.billingKey 상태 검증
* - customer.id == subscription.userId ?
* - dto.billingKey == subscription.billingKey ?
* - status != "DELETED" ?
*/

public interface BillingKeyVerifier {
    void verifyBillingKey(PortoneBillingKey portoneBillingKey, SubscriptionDto subscriptionDto);
}
