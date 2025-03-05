package ce2team1.mentoview.controller;

import ce2team1.mentoview.controller.dto.response.PaymentResp;
import ce2team1.mentoview.controller.dto.response.SubscriptionResp;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import ce2team1.mentoview.service.PaymentService;
import ce2team1.mentoview.service.PortonePaymentService;
import ce2team1.mentoview.service.SubscriptionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final PortonePaymentService portonePaymentService;

    @GetMapping("/subscription")
    public ResponseEntity<List<SubscriptionResp>> getSubscription(@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) {

        Long userId = mvPrincipalDetails.getUserId();
//        Long userId = 1L;

        List<SubscriptionResp> subscriptions = subscriptionService.getSubscriptions(userId);
        for (SubscriptionResp subscriptionResp : subscriptions) {
            List<PaymentResp> payments = paymentService.getPayment(subscriptionResp.getSubId());
            subscriptionResp.setPayments(payments);
        }

        return ResponseEntity.ok(subscriptions);
    }

    @DeleteMapping("/subscription/{subscription_id}")
    public ResponseEntity<String> deleteSubscription(@PathVariable("subscription_id") Long sId, @AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) throws JsonProcessingException {
        Long uId = mvPrincipalDetails.getUserId();
//        Long uId = 2L;
        Long checkSId = subscriptionService.checkSubscription(uId);

        if (checkSId != null && checkSId.equals(sId)) {
            // 결제 예약 취소
            portonePaymentService.cancelScheduling(sId);

            // 구독의 상태 변경
            subscriptionService.deleteSubscription(sId);
        }
        return ResponseEntity.ok("구독 해지 성공");

    }
}
