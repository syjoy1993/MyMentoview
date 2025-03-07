package ce2team1.mentoview.controller;

import ce2team1.mentoview.controller.dto.response.PaymentResp;
import ce2team1.mentoview.controller.dto.response.SubscriptionResp;
import ce2team1.mentoview.exception.SubscriptionException;
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
import org.springframework.http.HttpStatus;
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
//        Long userId = 1L;

        List<SubscriptionResp> subscriptions = subscriptionService.getSubscriptions(userId);
        for (SubscriptionResp subscriptionResp : subscriptions) {
            List<PaymentResp> payments = paymentService.getPayment(subscriptionResp.getSubId());
            subscriptionResp.setPayments(payments);
        }

        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "구독 상태 조회", description = "구독 처리 진행 상태를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 상태 조회를 성공하였습니다.")
    })
    @GetMapping("/subscription/status")
    public ResponseEntity<String> getSubscriptionStatus(@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) {

        Long userId = mvPrincipalDetails.getUserId();
//        Long userId = 1L;

        if (subscriptionService.getSubscriptionByUserId(userId) != null) {
            return ResponseEntity.ok("구독 처리 완료");
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @Operation(summary = "구독 해지", description = "결제 예약 취소 후 구독의 상태를 CANCELED로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 해지 성공")
    })
    @DeleteMapping("/subscription/{subscription_id}")
    public ResponseEntity<String> deleteSubscription(@PathVariable("subscription_id") Long sId, @AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) throws JsonProcessingException {

        Long uId = mvPrincipalDetails.getUserId();
//        Long uId = 1L;
        Long checkSId = subscriptionService.checkSubscription(uId);

        if (checkSId != null && checkSId.equals(sId)) {
            // 결제 예약 취소
            portonePaymentService.cancelScheduling(uId);

            // 구독의 상태 변경
            subscriptionService.deleteSubscription(sId);

            return ResponseEntity.ok("구독 해지 성공");
        } else {
            throw new SubscriptionException("해당 구독 내역은 유효하지 않습니다.", HttpStatus.BAD_REQUEST);

        }

    }

    @Operation(summary = "결제 생성", description = "포트원 서버로 결제를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 요청이 성공적으로 처리됐습니다."),
            @ApiResponse(responseCode = "500", description = "결제 요청 중 문제가 발생했습니다.")
    })
    @PostMapping("/subscription")
    public ResponseEntity<String> createSubscription(@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) throws JsonProcessingException {

        Long uId = mvPrincipalDetails.getUserId();
//        Long uId = 1L;

        try {
            portonePaymentService.createPayment(uId);
            return ResponseEntity.ok("결제가 정상적으로 처리되었습니다.");
        } catch (SubscriptionException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage()); // 예외에서 설정한 상태 코드와 메시지 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }
}
