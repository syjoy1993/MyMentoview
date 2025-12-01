package ce2team1.mentoview.payment.application.dto;

import ce2team1.mentoview.payment.infra.portone.dto.PortoneCustomer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
// 기존 PaymentCheckDto 대체
// Portone V2 Payment 객체 매핑
@Getter
public class PortonePayment {
    private String id;              // 포트원 거래고유번호 (Payment Entity -> transactionId)
    private String status;          // 결제 상태 (PAID, CANCELLED 등)
    private String transactionId;   // PG사 승인번호 (Payment Entity -> pgApprovalCode)
    private String merchantId;      // 주문번호 (Payment Entity -> merchantUid)
    private String storeId;
    private String billingKey;
    private Amount amount;          // 결제 금액 정보
    private String currency;
    private Method method;          // 결제 수단 정보
    private PortoneCustomer customer; // 공통 Customer DTO 사용
    private String paidAt;          // 결제 일시 (RFC3339)

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Method {
        private String type;
        private String provider;
        private EasyPayMethod easyPayMethod;
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EasyPayMethod {
        private String type;
    }

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        private BigDecimal total; // 총 금액
        private BigDecimal taxFree; // 면세 금액
        private BigDecimal vat; // 부가세
        private BigDecimal supply; // 공급가
        private BigDecimal discount; // 할인
        private BigDecimal paid; // 결제된 금액
        private BigDecimal cancelled; // 취소된 금액
    }


}

