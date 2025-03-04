package ce2team1.mentoview.controller;

import ce2team1.mentoview.controller.dto.request.BillingKeyCreate;
import ce2team1.mentoview.controller.dto.request.PaymentCreate;
import ce2team1.mentoview.service.PortonePaymentService;
import ce2team1.mentoview.service.SubscriptionService;
import ce2team1.mentoview.service.WebhookService;
import ce2team1.mentoview.service.dto.BillingKeyCheckDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;
    private final PortonePaymentService portonePaymentService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/payment")
    public ResponseEntity<?> createPayment(@RequestBody PaymentCreate payload,
                                           @RequestHeader Map<String, String> headers) throws JsonProcessingException {

        // 웹훅 검증
        System.out.println("결제 발생");
        boolean webhookChecked = webhookService.checkWebhook(payload, headers);

        // 웹훅 검증 실패
        if (!webhookChecked) {
            return ResponseEntity.badRequest().body("Invalid Webhook");
        }

        System.out.println("웹훅 검증 성공: " + payload.getType());

        // 결제가 된 상태 -> 처리 필요
        if ("Transaction.Paid".equals(payload.getType())) {

            try {
                // 결제 조회 -> 구독 저장 -> 결제 저장 -> 다음 결제 예약
                boolean success = portonePaymentService.checkPayment(payload);

                if (success) {
                    System.out.println("결제 검증 및 처리 성공");
                }
            } catch (Exception e) {
                // 결제 조회 실패 로그 남기기
                System.err.println("결제 조회 실패: " + e.getMessage());
            }
        } else if ("Transaction.Failed".equals(payload.getType())) {
            // 구독 조회해서 구독의 status 변경
            subscriptionService.deleteSubscriptionByPaymentId(payload.getData().getPaymentId());
        }

        // 웹훅 서버에는 항상 200 OK 응답
        // 웹훅 검증 성공 -> 포트원 서버에 웹훅 그만 날려도 된다고 200 OK 반환
        return ResponseEntity.ok("Webhook received successfully.");
    }


    @PostMapping("/billingkey")
    public ResponseEntity<?> createBillingKey(@RequestBody BillingKeyCreate payload,
                                              @RequestHeader Map<String, String> headers) throws JsonProcessingException {

        // 웹훅 검증
        boolean webhookChecked = webhookService.checkWebhook(payload, headers);

        // 웹훅 검증 실패
        if (!webhookChecked) {
            return ResponseEntity.badRequest().body("Invalid Webhook");
        }

        System.out.println("웹훅 검증 성공: " + payload.getType());

        // 유저 객체에 빌링키값 저장
        if ("BillingKey.Issued".equals(payload.getType())) {
            // 빌링키 조회
            BillingKeyCheckDto billingKeyCheckDto = portonePaymentService.checkBillingKey(payload.getData().getBillingKey());

            // BillingKey를 통해 결제 요청
            portonePaymentService.createPayment(Long.valueOf(billingKeyCheckDto.getCustomer().getId()), billingKeyCheckDto.getBillingKey());

        }

        return ResponseEntity.ok("Webhook received successfully.");
    }

    @PostMapping("/billingkey/modify")
    public ResponseEntity<?> modifyBillingKey(@RequestBody BillingKeyCreate payload,
                                              @RequestHeader Map<String, String> headers) throws JsonProcessingException {

        // 웹훅 검증
        boolean webhookChecked = webhookService.checkWebhook(payload, headers);

        // 웹훅 검증 실패
        if (!webhookChecked) {
            return ResponseEntity.badRequest().body("Invalid Webhook");
        }

        System.out.println("웹훅 검증 성공: " + payload.getType());

        // 유저 객체에 빌링키값 저장
        if ("BillingKey.Issued".equals(payload.getType())) {
            // 빌링키 조회
            BillingKeyCheckDto billingKeyCheckDto = portonePaymentService.checkBillingKey(payload.getData().getBillingKey());

            // 빌링키 변경 처리
            portonePaymentService.processChangingBillingKey(billingKeyCheckDto);
        }

        return ResponseEntity.ok("Webhook received successfully.");
    }
}
