package ce2team1.mentoview.payment.presentation;

import ce2team1.mentoview.subscription.presentation.dto.request.BillingKeyCreate;
import ce2team1.mentoview.payment.infra.portone.dto.PaymentCreate;
import ce2team1.mentoview.payment.infra.portone.WebhookVerifierAdapter;
import ce2team1.mentoview.payment.application.orchestrator.PortonePaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/webhook")
@Tag(name = "Webhook API", description = "포트원 서버의 웹훅을 전달받을 API (빌링키 발급, 결제 처리)")
@RequiredArgsConstructor
public class WebhookController {
    /*
     * todo
     *  - 전체 로직 확인 및 테스트 필요
     *  - swagger 문서화 점김 필요
     *  - 메서드 역할 분리 체크
     *
     * */


    private final WebhookVerifierAdapter webhookVerifierAdapter;
    private final PortonePaymentService portonePaymentService;
    private final ObjectMapper objectMapper;
    // 수정 완료
    @Operation(summary = "결제 발생 Webhook", description = "결제 관련(성공/실패/취소) 이벤트 수신처리")
    @PostMapping("/payment")
    public ResponseEntity<?> createPayment(@RequestBody String  rawBody,
                                           @RequestHeader Map<String, String> headers) throws JsonProcessingException {
        // 1. Webhook raw Body 검증
        if (!webhookVerifierAdapter.verifyWebhook(rawBody, headers)) {
            log.warn("결제 웹훅 검증 실패");
            return ResponseEntity.badRequest().body("Invalid Webhook Signature");
        }

        try {
            // parse rawBody to PaymentCreate object
            PaymentCreate paymentCreate = objectMapper.readValue(rawBody, PaymentCreate.class);

            portonePaymentService.handlePaymentWebhook(paymentCreate);

            return ResponseEntity.ok("Webhook received successfully.");
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 오류: ", e);
            return ResponseEntity.badRequest().body("Invalid JSON");
        } catch (Exception e) {
            log.error("웹훅 처리 중 오류: ", e);
            return ResponseEntity.internalServerError().body("Error processing webhook");
        }


    }
    //수정완료
    @Operation(summary = "BillingKey 발급 Webhook", description = "BillingKey  이벤트 수신")
    @PostMapping("/billingkey")
    public ResponseEntity<?> handleBillingkeyWebhook(@RequestBody String  rawBody,
                                              @RequestHeader Map<String, String> headers) throws JsonProcessingException {

        try {
            // 1. Webhook raw Body 검증
            if (!webhookVerifierAdapter.verifyWebhook(rawBody, headers)) {
                log.warn("BillingKey 웹훅 검증 실패");
                return ResponseEntity.badRequest().body("Invalid Webhook Signature");
            }
            BillingKeyCreate billingKeyData = objectMapper.readValue(rawBody, BillingKeyCreate.class);

            if ("BillingKey.Issued".equals(billingKeyData.getType())) {
                String customerId = billingKeyData.getData().getCustomer().getId();
                String billingKey = billingKeyData.getData().getBillingKey();

                String billingKeyMethod = billingKeyData.getData().getBillingKeyMethod();
                String issueName = billingKeyData.getData().getIssueName();

                log.info("빌링키 발급 웹훅 수신 - Customer ID: {}, Billing Key: {}, Method: {}, Issue Name: {}",
                        customerId, billingKey, billingKeyMethod, issueName);

                // 빌링기 저장 프로세스 시작
                portonePaymentService.registerBillingKeyFromWebhook(billingKey, customerId);
            }
            return ResponseEntity.ok("Webhook received successfully.");
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 오류: ", e);
            return ResponseEntity.badRequest().body("Invalid JSON");
        }
    }
    // d와
    @Operation(summary = "결제 수단 변경으로 빌링키 발급", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "웹훅 검증 성공")
    })
    @PostMapping("/billingkey/modify")
    public ResponseEntity<?> modifyBillingKey(@RequestBody String rawBody,
                                              @RequestHeader Map<String, String> headers) throws JsonProcessingException {
        try {
            if (!webhookVerifierAdapter.verifyWebhook(rawBody, headers)) {
                return ResponseEntity.badRequest().body("Invalid Webhook Signature");
            }

            BillingKeyCreate billingKeyData = objectMapper.readValue(rawBody, BillingKeyCreate.class);

            if ("BillingKey.Issued".equals(billingKeyData.getType())) {
                String customerId = billingKeyData.getData().getCustomer().getId();
                String newBillingKey = billingKeyData.getData().getBillingKey();

                // 변경 프로세스 실행
                portonePaymentService.processChangingBillingKey(newBillingKey, customerId);
            }
            return ResponseEntity.ok("Webhook received successfully.");
        } catch (Exception e) {
            log.error("빌링키 변경 웹훅 처리 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
