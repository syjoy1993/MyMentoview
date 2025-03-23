package ce2team1.mentoview.admin.adminrepository;

import ce2team1.mentoview.admin.admindto.request.AdminUserSearchCond;
import ce2team1.mentoview.admin.admindto.response.AdminUserDto;
import ce2team1.mentoview.entity.Payment;
import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.entity.User;
import ce2team1.mentoview.entity.atrribute.PaymentStatus;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import ce2team1.mentoview.entity.atrribute.UserStatus;
import ce2team1.mentoview.service.dto.UserDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminUserRepositoryImpl implements AdminUserRepositoryCustom {
    private final EntityManager em;

    @Override
    public Page<AdminUserDto> search(AdminUserSearchCond cond, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AdminUserDto> cq = cb.createQuery(AdminUserDto.class);
        Root<User> user = cq.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        if (cond.status() != null) {
            predicates.add(cb.equal(user.get("status"), cond.status()));
        } else {
            predicates.add(cb.equal(user.get("status"), UserStatus.ACTIVE)); // default : ACTIVE

            if (cond.email() != null && !cond.email().isEmpty()) {
                predicates.add(cb.like(user.get("email"), "%" + cond.email() + "%")); // 일부 검색허용 : like
            }
            if (cond.joinedDateStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(user.get("createdAt"), cond.joinedDateStart().atStartOfDay()));//.atStartOfDay() 00시부터
            }
            if (cond.joinedDateEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(user.get("createdAt"), cond.joinedDateEnd().atTime(LocalTime.MAX))); //해당날짜 자정 직전까지
            }

            Join<User, Subscription> userSubscriptionJoin = user.join("subscription", JoinType.LEFT);
            userSubscriptionJoin.on(cb.equal(userSubscriptionJoin.get("status"), cond.subscriptionStatus() != null ? cond.subscriptionStatus() : SubscriptionStatus.ACTIVE));
            // default : ACTIVE

            if (cond.subscriptionPlan() != null) {
                predicates.add(cb.equal(user.get("subscriptionPlan"), cond.subscriptionPlan()));
            }

            Join<Subscription, Payment> subscriptionPaymentJoin = userSubscriptionJoin.join("payments", JoinType.LEFT);
            subscriptionPaymentJoin.on(cb.equal(subscriptionPaymentJoin.get("status"), cond.paymentStatus() != null ? cond.paymentStatus() : PaymentStatus.SUCCESS));
            //default : SUCCESS

            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()]))); // (new Predicate[0]) 이거보다 조금 더 성능 우위에 있음

            cq.select(cb.construct(AdminUserDto.class,
                    user.get("userId"),
                    user.get("email"),
                    user.get("role"),
                    user.get("createdAt")));


        }
        return null;
    }
}