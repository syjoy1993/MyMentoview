package ce2team1.mentoview.service;


import ce2team1.mentoview.controller.dto.response.PaymentResp;
import ce2team1.mentoview.entity.Payment;
import ce2team1.mentoview.entity.Subscription;
import ce2team1.mentoview.repository.SubscriptionRepository;
import ce2team1.mentoview.service.dto.PaymentCheckDto;
import ce2team1.mentoview.repository.PaymentRepository;
import ce2team1.mentoview.service.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final SubscriptionService subscriptionService;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void createPayment(PaymentCheckDto paymentCheckDto, Long userId) {

        // 구독 생성 후 결제 저장
        Long subId = subscriptionService.createSubscription(userId, paymentCheckDto);
        Subscription subscription = subscriptionRepository.findById(subId).orElseThrow();

        Payment payment = PaymentDto.checkToDto(paymentCheckDto, subId).toEntity(subscription);
        paymentRepository.save(payment);
    }

    public List<PaymentResp> getPayment(Long subId) {

        return paymentRepository.findAllBySubId(subId)
                .stream()
                .map(payment -> {
                    PaymentResp resp = PaymentResp.toResp(PaymentDto.toDto(payment));
                    return resp;
                })
                .collect(Collectors.toList());

    }

}
