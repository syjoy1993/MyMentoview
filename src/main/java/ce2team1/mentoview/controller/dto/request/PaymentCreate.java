package ce2team1.mentoview.controller.dto.request;

import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.entity.atrribute.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PaymentCreate {
    @NotNull
    private BigDecimal amount;
    @NotBlank
    private String approvalCode;
    @NotNull
    private PaymentStatus status;
    @NotNull
    private LocalDateTime paymentDate;
    @NotNull
    private Subscription subscription;


}
