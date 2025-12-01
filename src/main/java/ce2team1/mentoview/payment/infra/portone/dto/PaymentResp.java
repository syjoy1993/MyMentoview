package ce2team1.mentoview.payment.infra.portone.dto;


import ce2team1.mentoview.payment.domain.attribute.PaymentStatus;
import ce2team1.mentoview.payment.domain.entity.Payment;
import ce2team1.mentoview.payment.application.dto.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link Payment}
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResp {
    /*
     * todo
     *  - PaymentResp, SubscriptionResp -> controller.dto.response 패키지 미사용 객체 정리 -> 사용후 패키지 명에 맞게 이동
     *  - response : 응답객체 시그니처 쳌크
     *  - 필요 객체 생성
     *  - 모두 완료후 패키지 명에 맞게 이동
     *
     * */

    private Long paymentId;
    private BigDecimal amount;
    private String approvalCode;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private Long subId;

    public static PaymentResp toResp(PaymentDto dto) {
        return PaymentResp.builder()
                .paymentId(dto.getPaymentId())
                .amount(dto.getAmount())
                .approvalCode(dto.getPgApprovalCode())
                .status(dto.getStatus())
                .paymentDate(dto.getPaymentDate())
                .subId(dto.getSubscriptionId())
                .build();
    }
}