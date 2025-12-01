package ce2team1.mentoview.subscription.presentation;

import ce2team1.mentoview.exception.SubscriptionException;
import ce2team1.mentoview.payment.application.orchestrator.PortonePaymentService;
import ce2team1.mentoview.payment.application.service.PaymentService;
import ce2team1.mentoview.payment.infra.portone.dto.PaymentResp;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import ce2team1.mentoview.subscription.application.service.SubscriptionService;
import ce2team1.mentoview.subscription.domain.attribute.SubscriptionStatus;
import ce2team1.mentoview.subscription.presentation.dto.response.SubscriptionResp;
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

    /*
     * todo : 고객 니즈 충족을 위한 최소 comtroller 부재 -> 구현하기
     *   -  구독상태 + 다음 결제정보 : 현재 로그인 유저의 활성 구독 한건만 조회
     *       - 포함 정보 :
     *           - 구독상태
     *           - 다음 결제 예정일
     *           - 다음 결제 금액
     *           - 사용중 결제수단요약정보()
     *          => SubscriptionCurrentResp
     */




    @Operation(summary = "구독 조회", description = "사용자의 모든 구독 내역과 구독 각각에 대한 결제 내역을 전달합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 조회에 성공했습니다.")
    })
    @GetMapping("/subscription")
    public ResponseEntity<List<SubscriptionResp>> getSubscription(@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) {

        Long userId = mvPrincipalDetails.getUserId();
        // Long userId = 1L;

        List<SubscriptionResp> subscriptions = subscriptionService.getSubscriptions(userId);
        for (SubscriptionResp subscriptionResp : subscriptions) {
            List<PaymentResp> payments = paymentService.getPayment(subscriptionResp.getSubId());
            subscriptionResp.setPayments(payments);
        }

        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "구독 상태 조회", description = "구독 처리 진행 상태를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 상태 조회에 성공했습니다.")
    })
    @GetMapping("/subscription/status")
    public ResponseEntity<String> getSubscriptionStatus(@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) {

        Long userId = mvPrincipalDetails.getUserId();
        // Long userId = 1L;

        if (subscriptionService.getSubscriptionByUserId(userId, SubscriptionStatus.ACTIVE) != null) {
            return ResponseEntity.ok("구독 처리 완료");
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @Operation(summary = "구독 해지", description = "결제 예약 취소 후 구독의 상태를 CANCELED로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 해지 요청이 성공적으로 처리됐습니다."),
            @ApiResponse(responseCode = "404", description = "해당 구독은 유효하지 않습니다."),
            @ApiResponse(responseCode = "500", description = "구독 해지 요청 처리 중 문제가 발생했습니다.")

    })
    @DeleteMapping("/subscription/{subscription_id}")
    public ResponseEntity<String> deleteSubscription(@PathVariable("subscription_id") Long sId, @AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) throws JsonProcessingException {

        Long uId = mvPrincipalDetails.getUserId();
        // Long uId = 1L;
        Long checkSId = subscriptionService.checkSubscription(uId);

        if (checkSId != null && checkSId.equals(sId)) {
            // 결제 예약 취소 후 빌링키 삭제
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
            @ApiResponse(responseCode = "500", description = "결제 요청 처리 중 문제가 발생했습니다.")
    })
    @PostMapping("/subscription")
    public ResponseEntity<String> createSubscription(@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) throws JsonProcessingException {

        Long uId = mvPrincipalDetails.getUserId();
        // Long uId = 1L;

        if (subscriptionService.getSubscriptionByUserId(uId, SubscriptionStatus.CANCELED) != null) {
            // 현재 CANCELED 상태의 구독의 nextBillingDate로 결제를 예약하고, 구독의 상태 ACTIVE로 변경
            portonePaymentService.processSubscriptionReactivation(uId);

        } else {
            // 바로 결제 진행
            portonePaymentService.createPayment(uId);

        }
        return ResponseEntity.ok("구독 요청이 정상적으로 처리되었습니다.");

    }

    @Operation(summary = "프리티어 구독 생성", description = "결제 없이 FREE-TIER 구독을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프리티어 구독 요청 처리가 성공적으로 이루어졌습니다."),
            @ApiResponse(responseCode = "500", description = "프리티어 구독 요청 처리 중 문제가 발생했습니다.")
    })
    @PostMapping("/subscription/freetier")
    public ResponseEntity<String> createSubscriptionFreetier(@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) throws JsonProcessingException {

        Long uId = mvPrincipalDetails.getUserId();
        // Long uId = 1L;

        // 첫 구독 -> free-tire로 구독 생성
        portonePaymentService.processFreeTierSubscription(uId);

        return ResponseEntity.ok("구독 요청이 정상적으로 처리되었습니다.");

    }
    @Operation(summary = "현재 활성 구독 요약 조회", description = "사용자의 활성 구독 상태, 다음 결제일, 금액 등을 조회합니다.")
    @GetMapping("/subscription/current")
    public ResponseEntity<?> getCurrentSubscription(@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails) {
        Long uId = mvPrincipalDetails.getUserId();

        // 1. 활성 구독 조회
        // Subscription subscription = subscriptionService.getSubscriptionByUserId(uId, SubscriptionStatus.ACTIVE);
        // if (subscription == null) return ResponseEntity.noContent().build();

        // 2. DTO 매핑 (SubscriptionCurrentResp 필요)
        // return ResponseEntity.ok(SubscriptionCurrentResp.from(subscription));

        // TODO: 구현 필요 (Service 레벨에서 DTO 리턴하도록)
        return ResponseEntity.ok("구독 정보 조회 API (구현 예정)");
    }
}
