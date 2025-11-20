package ce2team1.mentoview.payment.application;



import ce2team1.mentoview.payment.infra.portone.dto.PortoneBillingKey;
import ce2team1.mentoview.service.dto.PortonePayment;
import ce2team1.mentoview.service.dto.SubscriptionDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
// Portone 응답 + SubscriptionDto/BillingKeyDto
@Component
public class PortonePaymentVerifier implements PaymentVerifier, BillingKeyVerifier {
    // 결제 검증
    // Portone 응답 유효X -> 예외
    // expectedAmount : 금액비교, 현 default 10_000
    /*
     * todo
     *  - 검증 메서드 정합성 test
     *  - 검증이외 다른 작업 여부 체크
     *  - 사이드 이펙트 가능성 체크
     *  - 추가 검증 사항 문서 대조 비교 후 추가사항 명시 -> dto에 반영할 것
     *  - 현 로직 예외 처리 부 체크
     * */


    @Override
    public void verifyPayment(PortonePayment portonePayment, SubscriptionDto subscriptionDto, BigDecimal expectedAmount) {
        /*
        * todo
        *   - Portone 결제 응답 + SubscriptionDto + expectedAmount 정합성 검증 로직
        */
        if(portonePayment == null) throw new IllegalStateException("Invalid payment response");

        // 결제 상태 검증
        if(!"SUCCESS".equals(portonePayment.getStatus())) throw new IllegalStateException("Invalid payment status");

        // 결제금액 검증
        BigDecimal totalAmount = portonePayment.getAmount().getTotal();
        if(totalAmount.compareTo(expectedAmount) != 0) {
            throw new RuntimeException(String.format("Payment amount mismatch detected Expected: %s, Actual: %s", expectedAmount, totalAmount));
        }
        //통화 검증
        if(!"KRW".equals(portonePayment.getCurrency())) {
            throw new IllegalStateException("Invalid currency : " + portonePayment.getCurrency());
        }

        // 구독 정보 검증와 일치?
        if (subscriptionDto != null) {
            if (!String.valueOf(subscriptionDto.getUserId()).equals(portonePayment.getCustomer().getId())) {
                throw new IllegalStateException("Payment customer does not match subscription user");
            }

        }
    }

    // 빌링키 검증
    // 응답 : null, status==DELETED -> check 예외
    @Override
    public void verifyBillingKey(PortoneBillingKey keyCheckDto, SubscriptionDto subscriptionDto) {
        /*
         * todo
         *   - Portone 빌링키 응답 + SubscriptionDto 정합성 검증 로직
         */
        if (keyCheckDto == null) {
            throw new IllegalStateException("BillingKey response is null");
        }

        if ("DELETED".equals(keyCheckDto.getStatus())) {
            throw new IllegalStateException("BillingKey is DELETED");
        }

        // 구독 정보가 있다면 유저 매핑 검증
        if (subscriptionDto != null) {
            if (!String.valueOf(subscriptionDto.getUserId()).equals(keyCheckDto.getCustomer().getId())) {
                throw new IllegalStateException("BillingKey customer does not match subscription user");
            }
        }

    }
}
