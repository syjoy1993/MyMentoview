package ce2team1.mentoview.admin.admindto.request;

import ce2team1.mentoview.entity.atrribute.PaymentStatus;
import ce2team1.mentoview.entity.atrribute.SubscriptionPlan;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import ce2team1.mentoview.entity.atrribute.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record AdminUserSearchCond(
        @Email String email,
        @PastOrPresent LocalDate joinedDateStart,
        @PastOrPresent LocalDate joinedDateEnd,
        UserStatus status,
        SubscriptionStatus subscriptionStatus,
        SubscriptionPlan subscriptionPlan,
        PaymentStatus paymentStatus) {
}
