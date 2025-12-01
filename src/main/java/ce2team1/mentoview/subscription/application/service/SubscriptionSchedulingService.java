package ce2team1.mentoview.subscription.application.service;

import ce2team1.mentoview.subscription.domain.entity.Subscription;
import ce2team1.mentoview.subscription.domain.attribute.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class SubscriptionSchedulingService {
    private final SubscriptionService subscriptionService;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateSubscriptionStatusToExpiry() {

        LocalDate today = LocalDate.now();
        List<Subscription> expiredSubscriptions = subscriptionService.findCanceledSubscriptionsOfToday(today);

        for (Subscription subscription : expiredSubscriptions) {
            subscription.modifyStatus(SubscriptionStatus.EXPIRY);
        }

        System.out.println("만료된 구독 상태 업데이트 완료!");
    }
}
