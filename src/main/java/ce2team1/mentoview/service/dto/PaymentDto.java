package ce2team1.mentoview.service.dto;

import ce2team1.mentoview.entity.Payment;
import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.entity.atrribute.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.*;
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
    private String approvalCode;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private Long subId;

    private static PaymentDto of(BigDecimal amount, String approvalCode, PaymentStatus status, LocalDateTime paymentDate, Long subId) {
        return new PaymentDto(null, amount, approvalCode, status, paymentDate, subId);
    }
    private static PaymentDto of(Long paymentId, BigDecimal amount, String approvalCode, PaymentStatus status, LocalDateTime paymentDate, Long subId) {
        return new PaymentDto(paymentId, amount, approvalCode, status, paymentDate, subId);
    }

    public static PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .approvalCode(payment.getApprovalCode())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .subId(payment.getSubscription().getSubId())
                .build();

    }

    public static PaymentDto checkToDto(PaymentCheckDto paymentCheckDto, Long subId) {

        String paidAtString = paymentCheckDto.getPaidAt();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(paidAtString, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.systemDefault());

        return PaymentDto.of(
                paymentCheckDto.getAmount().getTotal(),
                paymentCheckDto.getTransactionId(),
                PaymentStatus.SUCCESS,
                localDateTime,
                subId);
    }

    public Payment toEntity(Subscription subscription) {
        return Payment.of(
                this.amount,
                this.approvalCode,
                this.getStatus(),
                this.getPaymentDate(),
                subscription
                );
    }

}