package ce2team1.mentoview.service;


import ce2team1.mentoview.controller.dto.response.SubscriptionResp;
import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.entity.User;
import ce2team1.mentoview.entity.atrribute.PaymentMethod;
import ce2team1.mentoview.entity.atrribute.SubscriptionPlan;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import ce2team1.mentoview.repository.SubscriptionRepository;
import ce2team1.mentoview.repository.UserRepository;
import ce2team1.mentoview.service.dto.PaymentCheckDto;
import ce2team1.mentoview.service.dto.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public List<SubscriptionResp> getSubscription(Long uId) {

        return subscriptionRepository.findAllByUserId(uId)
                                    .stream()
                                    .map(subscription -> {
                                        SubscriptionResp resp = SubscriptionResp.toResp(SubscriptionDto.toDto(subscription));
                                        return resp;
                                    })
                                    .collect(Collectors.toList());

    }

    @Transactional
    public void deleteSubscription(Long sId) {

        // 삭제 시 디비에서 삭제하는 게 아니라 status를 변경
        Subscription subscription = subscriptionRepository.findById(sId).orElseThrow();
        subscription.modifyStatusToCanceled();
    }

    public Long createSubscription(Long userId, PaymentCheckDto paymentCheckDto) {

        // paidAt 포맷팅
        String paidAtString = paymentCheckDto.getPaidAt();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(paidAtString, DateTimeFormatter.ISO_DATE_TIME);
        LocalDate ld = zonedDateTime.toLocalDate();

        System.out.println("유저를 못찾니?");
        User user = userRepository.findById(userId).orElseThrow();

        Subscription subscription = subscriptionRepository.save(Subscription.of(
                                                                    SubscriptionStatus.ACTIVE,
                                                                    SubscriptionPlan.PREMIUM,
                                                                    ld,
                                                                    ld.plusDays(30),
                                                                    ld.plusDays(31),
                                                                    PaymentMethod.KAKAO_PAY,
                                                                    user)
                                                                );

        return subscription.getSubId();
    }

}
