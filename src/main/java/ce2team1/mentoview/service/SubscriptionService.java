package ce2team1.mentoview.service;


import ce2team1.mentoview.controller.dto.response.SubscriptionResp;
import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.entity.User;
import ce2team1.mentoview.entity.atrribute.PaymentMethod;
import ce2team1.mentoview.entity.atrribute.SubscriptionPlan;
import ce2team1.mentoview.entity.atrribute.SubscriptionStatus;
import ce2team1.mentoview.repository.SubscriptionRepository;
import ce2team1.mentoview.repository.UserRepository;
import ce2team1.mentoview.service.dto.PortonePayment;
import ce2team1.mentoview.service.dto.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SubscriptionService {
    /*
     * Todo
     *  - BillingKey필드 User -> Subscription 변경 V
     *  - 불필요 메서드
     *  - UserRepository를 통해 User필드 변경 여부 체크
     *  - Service가 다른 계층이 하는 일을 하는지 체크
     *
     * */


    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<SubscriptionResp> getSubscriptions(Long subscriptionId) {
        return subscriptionRepository.findAllByUser_UserId(subscriptionId)
                .stream()
                .map(subscription -> SubscriptionResp.toResp(SubscriptionDto.toDto(subscription)))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSubscription(Long subscriptionId) {

        // 삭제 시 디비에서 삭제하는 게 아니라 status를 변경
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow();
        subscription.modifyStatus(SubscriptionStatus.CANCELED);
    }

    @Transactional
    public void deleteSubscriptionByPaymentId(String paymentId) {

        // 삭제 시 디비에서 삭제하는 게 아니라 status를 변경
        Subscription subscription = subscriptionRepository.findByPortonePaymentId(paymentId);
        if (subscription != null) {
            subscription.modifyStatus(SubscriptionStatus.EXPIRY);
        }
    }

    // PortonePayment 적용
    public SubscriptionDto createSubscription(PortonePayment portonePayment) {
        // paidAt 포맷팅 (ISO 8601 -> LocalDate)
        String paidAtString = portonePayment.getPaidAt();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(paidAtString, DateTimeFormatter.ISO_DATE_TIME)
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"));;
        LocalDate localDate = zonedDateTime.toLocalDate();

        // Customer ID 파싱
        Long userId = Long.valueOf(portonePayment.getCustomer().getId());
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 결제 수단 판별
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        if (portonePayment.getMethod() != null && "KAKAOPAY".equals(portonePayment.getMethod().getProvider())) {
            paymentMethod = PaymentMethod.KAKAO_PAY;
        }

        // Subscription 생성
        Subscription savedSubscription = subscriptionRepository.save(Subscription.of(
                SubscriptionStatus.ACTIVE,
                SubscriptionPlan.BASIC,
                localDate,                     // startDate
                localDate.plusDays(30),        // endDate
                localDate.plusDays(31),        // nextBillingDate
                paymentMethod,
                portonePayment.getBillingKey(), // ✅ 빌링키 저장
                // paymentId (초기값)
                null,                   // scheduleId (String) - null 허용
                user)
        );

        return SubscriptionDto.toDto(savedSubscription);
    }

    public void createFreeTierSubscription(Long userId) {

        LocalDate localDate = LocalDate.now();
        User user = userRepository.findById(userId).orElseThrow();

        // ✅ 기존 빌링키가 있다면 유지 (나중에 유료 전환 시 사용 가능)
        String existingKey = getBillingKey(userId);

        subscriptionRepository.save(Subscription.of(
                SubscriptionStatus.ACTIVE,
                SubscriptionPlan.FREE_TIER,
                localDate,
                localDate.plusDays(30),
                localDate.plusDays(31),
                PaymentMethod.KAKAO_PAY,
                existingKey, // ✅ 기존 키 유지
                null,
                user)
        );
    }
    @Transactional(readOnly = true)
    public Long checkSubscription(Long uId) {
        Subscription subscription = subscriptionRepository.findByUser_UserIdAndStatus(uId, SubscriptionStatus.ACTIVE);
        return (subscription != null) ? subscription.getSubId() : null;
    }

    @Transactional
    public SubscriptionDto modifyEndDateAndNextBillingDate(Long subscriptionId, String paidAt) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow();
        subscription.modifyEndDateAndNextBillingDateAndPlan(paidAt);

        return SubscriptionDto.toDto(subscription);
    }


    @Transactional
    public void initPaymentScheduleIdAndPaymentId(Long uId, String paymentId, String scheduleId) {
        Subscription subscription = subscriptionRepository.findByUser_UserIdAndStatus(uId, SubscriptionStatus.ACTIVE);

        if (subscription == null) {
            subscription = subscriptionRepository.findByUser_UserIdAndStatus(uId, SubscriptionStatus.CANCELED);
        }
        // NPE 방지
        if (subscription != null) {
            subscription.setPaymentIdAndScheduleId(scheduleId);
        }
    }

    @Transactional(readOnly = true)
    public Subscription getSubscriptionByUserId(Long uId, SubscriptionStatus status) {
        return subscriptionRepository.findByUser_UserIdAndStatus(uId, status);

    }

    @Transactional(readOnly = true)
    public List<Subscription> findCanceledSubscriptionsOfToday(LocalDate today) {

        return subscriptionRepository.findByStatusAndNextBillingDate(SubscriptionStatus.CANCELED, today);
    }


    @Transactional
    public void modifySubscriptionStatusToActive(Long userId, SubscriptionStatus subscriptionStatus) {
        Subscription subscription = getSubscriptionByUserId(userId, subscriptionStatus);
        if (subscription != null) {
            subscription.modifyStatus(SubscriptionStatus.ACTIVE);
        }
    }
    /**
     * 빌링키 등록/갱신
     * - 활성 구독이 있으면 해당 구독의 빌링키 갱신
     * - 없으면 가장 최근 구독 갱신
     * - 구독 이력이 아예 없으면 'CANCELED' 상태의 플레이스홀더 구독 생성하여 저장
     */
    @Transactional
    public void registerBillingKey(Long userId, String billingKey) {
        Subscription latestSubscription = findLatestSubscription(userId);

        if (latestSubscription != null) {
            latestSubscription.modifyBillingKey(billingKey);
        } else {
            // 구독 이력이 없는 유저가 카드만 등록한 경우 -> CANCELED 상태로 키 저장용 구독 생성
            User user = userRepository.findById(userId).orElseThrow();
            subscriptionRepository.save(Subscription.of(
                    SubscriptionStatus.CANCELED, // 활성화 전
                    SubscriptionPlan.BASIC,
                    LocalDate.now(), LocalDate.now(), LocalDate.now(), // 의미 없는 날짜
                    PaymentMethod.CREDIT_CARD,
                    billingKey,
                    null, user
            ));
        }

    }
    @Transactional
    public void updateBillingKey(Long userId, String newBillingKey) {
        registerBillingKey(userId, newBillingKey);
    }
    @Transactional(readOnly = true)
    public String getBillingKey(Long userId) {
        Subscription latestSubscription = findLatestSubscription(userId);
        return latestSubscription != null ? latestSubscription.getBillingKey() : null;
    }
    @Transactional
    public void deleteBillingKey(Long userId) {
        Subscription latestSubscription = findLatestSubscription(userId);
        if (latestSubscription != null) {
            latestSubscription.modifyBillingKey(null);
        }
    }
    // 유저의 가장 최근 구독 조회 (Active 우선, 없으면 ID 역순)

    private Subscription findLatestSubscription(Long userId) {
        // 활성 구독 우선 조회
        Subscription activeSub = subscriptionRepository.findByUser_UserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
        if (activeSub != null) return activeSub;

        // 없으면 전체 중 가장 최신 조회
        return subscriptionRepository.findAllByUser_UserId(userId).stream()
                .max(Comparator.comparing(Subscription::getSubId))
                .orElse(null);
    }
}
