package ce2team1.mentoview.subscription.application.service;

import ce2team1.mentoview.subscription.domain.attribute.SubscriptionStatus;
import ce2team1.mentoview.subscription.domain.entity.Subscription;
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
    /*
     * Todo : Notion check
     *  - PortOne 결제 결과와 상태 정렬
     *  -  스케줄 누락/지연 대비
     *  - 타임존/시간 소스 고정
     *  - 조회 범위/성능 최적화
     *  - 동시성·경합 시나리오 정의(같은 시점에 웹훅(결제 성공/실패), 관리자 수동 취소, 배치가 동시에 상태를 바꿀 수 있는지 검토.)
     *  - 로깅·모니터링 추가(배치 1회 실행당:
            전체 조회 수, 상태 변경 건수, 예외/스킵 건수, Prometheus 메트릭, 알람 훅 연결 후보로 )
     *  - 경계 케이스 테스트(오늘/어제/내일 nextBillingDate,월말/윤년 날짜 전이 )
     *  - 스케줄 설정 외부화
     *      - cron 표현식 ("0 0 0 * * *") 을 설정(YAML)로 분리.
     *      - 환경별(스테이징/운영) 혹은 요금제 정책 변경 시 배치 시간 유연하게 조정 가능하게 하기.
     * */


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
