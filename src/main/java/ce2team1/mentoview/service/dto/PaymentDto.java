package ce2team1.mentoview.service.dto;

import ce2team1.mentoview.entity.Payment;
import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.entity.atrribute.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO for {@link Payment}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentDto {

    private Long paymentId;
    private BigDecimal amount;
    private String pgApprovalCode; // 이름 변경 (approvalCode -> pgApprovalCode)
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String transactionId; // 추가 포트원 거래 ID
    private String merchantUid;   // 추가
    private Long subscriptionId;

    // --- Entity -> DTO 변환 ---
    public static PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .pgApprovalCode(payment.getPgApprovalCode()) // 변경된 필드
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .transactionId(payment.getTransactionId()) // 추가된 필드
                .merchantUid(payment.getMerchantUid())     // 추가된 필드
                .subscriptionId(payment.getSubscription().getSubId())
                .build();
    }
    public static PaymentDto fromPortone(PortonePayment portonePayment, Long subId) {
        // 시간 파싱 로직 (Portone ISO String -> LocalDateTime)
        // (기존 로직 유지하되, 필요시 개선)
        LocalDateTime payDate = LocalDateTime.now(); // 임시
        if (portonePayment.getPaidAt() != null) {
            payDate = ZonedDateTime.parse(portonePayment.getPaidAt(), DateTimeFormatter.ISO_DATE_TIME)
                    .toLocalDateTime();
        }

        return PaymentDto.builder()
                .amount(portonePayment.getAmount().getTotal())
                // 승인번호가 없으면 transactionId로 대체하는 등의 방어 로직
                .pgApprovalCode(portonePayment.getTransactionId() != null ? portonePayment.getTransactionId() : "N/A")
                .status(PaymentStatus.SUCCESS) // 보통 체크 후 저장
                .paymentDate(payDate)
                .transactionId(portonePayment.getId())       // 포트원 ID, Entity: transactionId
                .merchantUid(portonePayment.getMerchantId()) // 주문번호
                .subscriptionId(subId)
                .build();
    }

    // --- DTO -> Entity 변환 ---
    public Payment toEntity(Subscription subscription) {
        return Payment.builder()
                .amount(this.amount)
                .pgApprovalCode(this.pgApprovalCode)
                .status(this.status)
                .paymentDate(this.paymentDate)
                .transactionId(this.transactionId)
                .merchantUid(this.merchantUid)
                .subscription(subscription)
                .build();
    }

}