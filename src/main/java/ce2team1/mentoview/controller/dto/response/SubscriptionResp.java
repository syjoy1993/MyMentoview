package ce2team1.mentoview.controller.dto.response;

import ce2team1.mentoview.entity.atrribute.SubscriptionPlan;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import ce2team1.mentoview.service.dto.PaymentDto;
import ce2team1.mentoview.service.dto.SubscriptionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link ce2team1.mentoview.entity.Subscription}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResp {

    Long subId;
    SubscriptionStatus status;
    SubscriptionPlan plan;
    LocalDate startDate;
    LocalDate endDate;
    LocalDate nextBillingDate;
    String paymentMethod;
    Long userId;
    List<PaymentResp> payments;

    public static SubscriptionResp toResp(SubscriptionDto dto) {
        return SubscriptionResp.builder()
                .subId(dto.getSubId())
                .status(dto.getStatus())
                .plan(dto.getPlan())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .nextBillingDate(dto.getNextBillingDate())
                .paymentMethod(dto.getPaymentMethod().toString())
                .userId(dto.getUserId())
                .build();

    }

    public void setPayments(List<PaymentResp> payments) {
        this.payments = payments;
    }
}