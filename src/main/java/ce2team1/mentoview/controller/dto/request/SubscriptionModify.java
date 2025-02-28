package ce2team1.mentoview.controller.dto.request;

import ce2team1.mentoview.entity.atrribute.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionModify {
    @NotNull
    Long subId;
    SubscriptionPlan plan;
    String paymentMethod;
    @NotNull
    Long userId;
}
