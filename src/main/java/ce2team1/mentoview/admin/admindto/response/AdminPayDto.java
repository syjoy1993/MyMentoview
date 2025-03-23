package ce2team1.mentoview.admin.admindto.response;

import ce2team1.mentoview.entity.atrribute.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminPayDto(Long paymentId,
                          LocalDateTime paymentDate,
                          BigDecimal amount,
                          PaymentStatus status) {
}
