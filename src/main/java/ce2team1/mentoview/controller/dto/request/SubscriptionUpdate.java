package ce2team1.mentoview.controller.dto.request;


import ce2team1.mentoview.entity.atrribute.SubscriptionPlan;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SubscriptionUpdate {
    @NotNull
    private Long subId;
    @NotBlank
    private SubscriptionStatus status;
    @NotBlank
    private SubscriptionPlan plan;
    @NotBlank
    private LocalDate startDate;
    @NotBlank
    private LocalDate endDate;
    @NotBlank
    private LocalDate nextBillingDate;
    @NotBlank
    private String paymentMethod;
    @NotNull
    private Long userId;


}
