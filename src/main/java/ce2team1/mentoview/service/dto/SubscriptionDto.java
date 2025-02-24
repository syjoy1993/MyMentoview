package ce2team1.mentoview.service.dto;


import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.entity.atrribute.PaymentMethod;
import ce2team1.mentoview.entity.atrribute.SubscriptionPlan;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for {@link Subscription}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscriptionDto {
    private Long subId;
    private SubscriptionStatus status;
    private SubscriptionPlan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;
    private PaymentMethod paymentMethod;
    private Long userId;

    private static SubscriptionDto of(SubscriptionStatus status, SubscriptionPlan plan, LocalDate startDate, LocalDate endDate, LocalDate nextBillingDate, PaymentMethod paymentMethod, Long userId) {
        return new SubscriptionDto(null, status, plan, startDate, endDate, nextBillingDate, paymentMethod, userId);
    }
    private static SubscriptionDto of(Long subId, SubscriptionStatus status, SubscriptionPlan plan, LocalDate startDate, LocalDate endDate, LocalDate nextBillingDate, PaymentMethod paymentMethod,Long userId) {
        return new SubscriptionDto(subId, status, plan, startDate, endDate, nextBillingDate, paymentMethod, userId);
    }


    public static SubscriptionDto toDto(Subscription subscription ) {
        return SubscriptionDto.builder()
                .subId(subscription.getSubId())
                .status(subscription.getStatus())
                .plan(subscription.getPlan())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .nextBillingDate(subscription.getNextBillingDate())
                .paymentMethod(subscription.getPaymentMethod())
                .userId(subscription.getUser().getUserId())
                .build();

    }


}