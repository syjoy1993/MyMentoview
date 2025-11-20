package ce2team1.mentoview.payment.presentation;

import ce2team1.mentoview.controller.dto.response.PaymentResp;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import ce2team1.mentoview.service.PaymentService;
import ce2team1.mentoview.service.PortonePaymentService;
import ce2team1.mentoview.service.SubscriptionService;
import ce2team1.mentoview.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Payment API", description = "결제 이력 및 결제 수단 관리")
public class PaymentController {
    /*
    * todo
    *   1. GET /api/payments
    *       - 현재 유저의 최근 결제 내역 목록
    *       - 결제수단, 결제 일시, 상태
    *   2. GET /api/payments/{paymentId}
    *       - 단일 결제 상세 정보 조회
    *       - 금액, 상태, 결제수단,
    */

    /*
     * todo : 결제 수단 관리 API
     *   1. GET /api/billing-key
     *   -  현재 유저의 활성 빌링키/결제수단 정보 조회
     *   -  status, pgProvider, issuedAt, 마지막 사용일, 마스킹된 카드 정보 등 (가능한 범위)
     *   2. DELETE /api/billing-key
     *   - 결제수단 해지 요청
     *   - Portone payment-schedules 취소 + billing-keys 삭제 + 우리 Subscription/User 반영
     *      -> 흩어진거 모아오기
     * => PaymentMethodController 별도?
     *
     */

    //프레젠테이션 전용 ㅅsesrvice
    //private final PaymentQueryService paymentQueryService;

    private final PaymentService paymentService;
    private final PortonePaymentService portonePaymentService;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Operation(summary = "내 결제 이력 조회", description = "현재 유저의 모든 결제 내역을 조회합니다.")
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResp>> getMyPayments(@AuthenticationPrincipal MvPrincipalDetails user) {
        // PaymentService 내부에 findByUserId 등을 구현했다고 가정, 혹은 subscriptionId로 조회
        // 여기서는 예시로 SubscriptionService를 경유하거나 PaymentRepository 직접 호출을 지양하므로 Service 메소드 호출
        // 임시로 기존에 있는 getPayment(subId) 활용이 어려우므로, user ID 기반 조회 메서드가 필요함.
        // List<PaymentResp> payments = paymentService.getPaymentsByUserId(user.getUserId());
        return ResponseEntity.ok(List.of()); // TODO: Service에 메서드 추가 필요
    }

    @Operation(summary = "결제 수단(빌링키) 조회", description = "현재 등록된 결제 수단의 정보를 조회합니다.")
    @GetMapping("/billing-key")
    public ResponseEntity<?> getMyBillingKey(@AuthenticationPrincipal MvPrincipalDetails user) {
        String billingKey = subscriptionService.getBillingKey(user.getUserId());
        if (billingKey == null) {
            return ResponseEntity.noContent().build();
        }

        // 포트원 API를 통해 상세 정보(카드번호 마스킹 등)를 가져와서 보여줄 수도 있음
        // PortoneBillingKey info = portonePaymentService.checkBillingKey(billingKey); // public으로 열어야 함
        return ResponseEntity.ok(billingKey);
    }

    @Operation(summary = "결제 수단 해지", description = "등록된 빌링키를 삭제하고 구독 예약을 취소합니다.")
    @DeleteMapping("/billing-key")
    public ResponseEntity<String> deleteBillingKey(@AuthenticationPrincipal MvPrincipalDetails user) {
        try {
            // 오케스트레이터에게 위임 (스케줄 취소 + 빌링키 삭제 + DB null 처리)
            portonePaymentService.cancelScheduling(user.getUserId());
            return ResponseEntity.ok("결제 수단이 해지되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("해지 중 오류 발생: " + e.getMessage());
        }
    }


}
