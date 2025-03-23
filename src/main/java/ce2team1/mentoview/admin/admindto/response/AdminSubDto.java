package ce2team1.mentoview.admin.admindto.response;

import ce2team1.mentoview.entity.atrribute.PaymentMethod;
import ce2team1.mentoview.entity.atrribute.SubscriptionPlan;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import ce2team1.mentoview.service.dto.PaymentDto;

import java.time.LocalDate;

public record AdminSubDto(
                        Long subscriptionId,
                        SubscriptionStatus status,
                        SubscriptionPlan plan,
                        PaymentMethod paymentMethod,
                        LocalDate startDate,
                        LocalDate endDate
                        //AdminPayDto payment
                        ) {

}
