package ce2team1.mentoview.controller;

import ce2team1.mentoview.controller.dto.response.PaymentResp;
import ce2team1.mentoview.controller.dto.response.SubscriptionResp;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import ce2team1.mentoview.service.PaymentService;
import ce2team1.mentoview.service.PortonePaymentService;
import ce2team1.mentoview.service.SubscriptionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@Tag(name = "Subscription API", description = "구독 조회, 구독 해지 API")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final PortonePaymentService portonePaymentService;

    @Operation(summary = "구독 조회", description = "사용자의 모든 구독 내역과 구독 각각에 대한 결제 내역을 전달합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 조회 성공")
    })
    @GetMapping("/subscription")
    public ResponseEntity<List<SubscriptionResp>> getSubscription(@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) {

        Long userId = mvPrincipalDetails.getUserId();
//        Long userId = 2L;

        List<SubscriptionResp> subscriptions = subscriptionService.getSubscriptions(userId);
        for (SubscriptionResp subscriptionResp : subscriptions) {
            List<PaymentResp> payments = paymentService.getPayment(subscriptionResp.getSubId());
            subscriptionResp.setPayments(payments);
        }

        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "구독 해지", description = "결제 예약 취소 후 구독의 상태를 CANCELED로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 해지 성공")
    })
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
