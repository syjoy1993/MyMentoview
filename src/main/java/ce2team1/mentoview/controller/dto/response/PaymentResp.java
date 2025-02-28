package ce2team1.mentoview.controller.dto.response;


import ce2team1.mentoview.entity.atrribute.PaymentStatus;
import ce2team1.mentoview.service.dto.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link ce2team1.mentoview.entity.Payment}
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResp {

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
                .approvalCode(dto.getApprovalCode())
                .status(dto.getStatus())
                .paymentDate(dto.getPaymentDate())
                .subId(dto.getSubId())
                .build();
    }
}