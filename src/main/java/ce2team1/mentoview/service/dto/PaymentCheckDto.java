package ce2team1.mentoview.service.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentCheckDto {
    private Amount amount; // 결제 금액
    private String id; // 포트원 거래고유번호
    private String merchantId; // 고객사 주문번호
    private String status; // 결제 상태
    private String currency; // 결제통화 구분코드
    private String transactionId; // 승인번호
    private String billingKey;
    private String paidAt; // 결제 일시
    private Method method; // 결제방법
    private Customer customer; // 사용자 정보

    @Getter
    public static class Method {
        private String type;
        private String provider;
        private EasyPayMethod easyPayMethod;
    }

    @Getter
    public static class EasyPayMethod {
        private String type;
    }

    @Getter
    public static class Amount {
        private BigDecimal total; // 총 금액
        private BigDecimal taxFree; // 면세 금액
        private BigDecimal vat; // 부가세
        private BigDecimal supply; // 공급가
        private BigDecimal discount; // 할인
        private BigDecimal paid; // 결제된 금액
        private BigDecimal cancelled; // 취소된 금액
    }

    @Getter
    public static class Customer {
        private String id;
    }
}

