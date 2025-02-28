package ce2team1.mentoview.entity;


import ce2team1.mentoview.entity.atrribute.AuditingFields;
import ce2team1.mentoview.entity.atrribute.PaymentMethod;
import ce2team1.mentoview.entity.atrribute.SubscriptionPlan;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Subscription extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan plan;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Subscription of(SubscriptionStatus status, SubscriptionPlan plan, LocalDate startDate, LocalDate endDate, LocalDate nextBillingDate, PaymentMethod paymentMethod, User user) {
        return new Subscription(null,status, plan, startDate, endDate, nextBillingDate, paymentMethod, user);

    }
    public static Subscription of(Long subId, SubscriptionStatus status, SubscriptionPlan plan, LocalDate startDate, LocalDate endDate,LocalDate nextBillingDate, PaymentMethod paymentMethod, User user) {
        return new Subscription(subId,status, plan, startDate, endDate, nextBillingDate, paymentMethod, user);

    }





    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Subscription that = (Subscription) o;
        return getSubId() != null && Objects.equals(getSubId(), that.getSubId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


    public void modifyStatusToCanceled() {
        this.status = SubscriptionStatus.CANCELED;
    }
}
