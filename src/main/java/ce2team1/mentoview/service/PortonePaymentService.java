package ce2team1.mentoview.service;

import ce2team1.mentoview.controller.dto.request.PaymentCreate;
import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import ce2team1.mentoview.exception.SubscriptionException;
import ce2team1.mentoview.payment.application.PortonePaymentVerifier;
import ce2team1.mentoview.payment.infra.portone.PortoneApiClient;
import ce2team1.mentoview.payment.infra.portone.dto.PortoneBillingKey;
import ce2team1.mentoview.service.dto.PortonePayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortonePaymentService {

    private final PortoneApiClient apiClient;
    private final PaymentService paymentService;
    private final SubscriptionService subscriptionService;
    private final PortonePaymentVerifier verifier;


    /*
     * todo
     *   - validatePayment(0 메서드 내 검증 로직, 비즈니스 로직 분리
     *   - validateBillingKey() 외부 검증 + User Update 분리
     *   - processChangingBillingKey() 외부 API(Portone 3 + db) + 도메인 변경
     *   - Reacvite 프로그래밍 -> timeout() Operator부재 -> 변경
     *      - Backpressure 처리 필요
     *      - error handling -> try~catch -> 다른 Operator 활용
     */

    //결제 웹훅 처리 (Orchestration)
    public void handlePaymentWebhook(PaymentCreate payload) {
        String type = payload.getType();
        String paymentId = payload.getData().getPaymentId();

        log.info("결제 웹훅 처리 시작. Type: {}, PaymentId: {}", type, paymentId);

        if ("Transaction.Paid".equals(type)) {
            processPaidWebhook(paymentId);

        } else if ("Transaction.Failed".equals(type)) {
            processFailedWebhook(paymentId);
        } else {
            log.warn("지원하지 않는 결제 웹훅 타입입니다: {}", type);
        }
    }

    private void processPaidWebhook(String paymentId) {
        try {
            // 1. API 호출
            PortonePayment portonePayment = apiClient.getPayment(paymentId);

            // 2. 검증
            verifier.verifyPayment(portonePayment, null, BigDecimal.valueOf(10000));

            // 3. 도메인 로직 (결제 저장 및 구독 갱신)
            paymentService.createPayment(portonePayment);

            // 4. 다음 결제 예약
            // paidAt 기준 31일 뒤 계산은 여기서 수행하여 ApiClient에 전달
            OffsetDateTime paidAt = OffsetDateTime.parse(portonePayment.getPaidAt());
            OffsetDateTime nextBillingDate = paidAt.plusDays(31).withOffsetSameInstant(ZoneOffset.of("+09:00"));

            schedulePaymentStep(
                    Long.valueOf(portonePayment.getCustomer().getId()),
                    portonePayment.getBillingKey(),
                    nextBillingDate
            );
        } catch (Exception e) {
            log.error("Transaction.Paid 처리 실패 paymentId={}", paymentId, e);
            throw new RuntimeException("Webhook processing failed", e);
        }
    }
    private void processFailedWebhook(String paymentId) {
        subscriptionService.deleteSubscriptionByPaymentId(paymentId);
    }

    //빌링키 등록 (웹훅)
    public void registerBillingKeyFromWebhook(String billingKey, String customerId) {
        try {
            // 1. 조회
            PortoneBillingKey response = apiClient.getBillingKey(billingKey);

            // 2. 검증
            if (!response.getCustomer().getId().equals(customerId)) {
                throw new IllegalStateException("BillingKey customer mismatch");
            }
            verifier.verifyBillingKey(response, null);

            // 3. 저장
            subscriptionService.registerBillingKey(Long.valueOf(customerId), billingKey);
            log.info("빌링키 등록 완료 user={}, key={}", customerId, billingKey);

        } catch (Exception e) {
            log.error("BillingKey 등록 실패", e);
            throw new RuntimeException("BillingKey registration failed", e);
        }
    }

    // 단순 조회 + 검증
    public PortoneBillingKey checkBillingKey(String billingKey) {
        PortoneBillingKey response = apiClient.getBillingKey(billingKey);
        verifier.verifyBillingKey(response, null);
        return response;
    }

    // 빌링키 변경 (재발급)
    public void processChangingBillingKey(String newBillingKey, String customerId) {
        Long uId = Long.valueOf(customerId);

        // 1. 검증
        PortoneBillingKey newKeyDto = apiClient.getBillingKey(newBillingKey);
        verifier.verifyBillingKey(newKeyDto, null);

        // 2. 활성 구독 확인
        Subscription subscription = subscriptionService.getSubscriptionByUserId(uId, SubscriptionStatus.ACTIVE);
        if (subscription == null) {
            // 구독이 없으면 단순 키 업데이트
            subscriptionService.updateBillingKey(uId, newBillingKey);
            return;
        }

        // 3. 기존 스케줄 시간 확인
        String timeToPayStr = apiClient.getScheduleTimeToPay(subscription.getPortoneScheduleId());

        // 4. 기존 스케줄 취소
        cancelScheduling(uId);

        // 5. 새 빌링키로 재예약
        if (timeToPayStr != null) {
            OffsetDateTime timeToPay = OffsetDateTime.parse(timeToPayStr);
            schedulePaymentStep(uId, newBillingKey, timeToPay);
        }

        // 6. 저장
        subscriptionService.updateBillingKey(uId, newBillingKey);
    }

    //결제 생성 (수동 요청)
    public void createPayment(Long uId) {
        String billingKey = subscriptionService.getBillingKey(uId);
        if (billingKey == null) {
            throw new SubscriptionException("등록된 결제 수단이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        String paymentId = "payment-" + UUID.randomUUID();

        try {
            apiClient.createPayment(paymentId, uId, billingKey, "월간 이용권 정기결제", 10000);
            // 이후 로직은 Webhook(Paid)이 들어오면서 처리됨
        } catch (Exception e) {
            throw new SubscriptionException("결제 요청 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 스케줄 취소 및 빌링키 삭제
    public void cancelScheduling(Long uId) {
        String billingKey = subscriptionService.getBillingKey(uId);
        if (billingKey == null) return;

        try {
            apiClient.cancelSchedules(billingKey);
        } catch (Exception e) {
            log.warn("스케줄 취소 실패 (이미 취소되었거나 없음): {}", e.getMessage());
        }

        try {
            apiClient.deleteBillingKey(billingKey);
        } catch (Exception e) {
            log.warn("빌링키 삭제 실패: {}", e.getMessage());
        }

        // 빌링키 삭제 (null 처리)
        subscriptionService.deleteBillingKey(uId);
    }
    private void schedulePaymentStep(Long uId, String billingKey, OffsetDateTime paymentDate) {
        String paymentId = "payment-" + UUID.randomUUID();
        try {
            String scheduleId = apiClient.schedulePayment(paymentId, uId, billingKey, paymentDate);
            if (scheduleId != null) {
                subscriptionService.initPaymentScheduleIdAndPaymentId(uId, paymentId, scheduleId);
            }
        } catch (Exception e) {
            log.error("결제 예약 실패 uId={}", uId, e);
            throw new SubscriptionException("결제 예약 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void processSubscriptionReactivation(Long uId) {
        Subscription subscription = subscriptionService.getSubscriptionByUserId(uId, SubscriptionStatus.CANCELED);
        String billingKey = subscriptionService.getBillingKey(uId);

        // LocalDate -> ZonedDateTime -> OffsetDateTime 변환 및 TimeZone 맞춤
        OffsetDateTime nextBillingDate = subscription.getNextBillingDate()
                .atStartOfDay(ZoneOffset.UTC) // return ZonedDateTime
                .toOffsetDateTime()           // ✅ ZonedDateTime -> OffsetDateTime 변환
                .withOffsetSameInstant(ZoneOffset.of("+09:00"));

        schedulePaymentStep(uId, billingKey, nextBillingDate);
        subscriptionService.modifySubscriptionStatusToActive(uId, SubscriptionStatus.CANCELED);
    }


    public void processFreeTierSubscription(Long uId) {
        String billingKey = subscriptionService.getBillingKey(uId);
        subscriptionService.createFreeTierSubscription(uId);

        // LocalDate -> ZonedDateTime -> OffsetDateTime 변환
        OffsetDateTime nextMonth = LocalDate.now().plusDays(31)
                .atStartOfDay(ZoneOffset.UTC) // return ZonedDateTime
                .toOffsetDateTime()           // ✅ ZonedDateTime -> OffsetDateTime 변환
                .withOffsetSameInstant(ZoneOffset.of("+09:00"));

        schedulePaymentStep(uId, billingKey, nextMonth);
    }

}
