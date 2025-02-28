package ce2team1.mentoview.service;

import ce2team1.mentoview.controller.dto.request.PaymentCreate;
import ce2team1.mentoview.service.dto.BillingKeyCheckDto;
import ce2team1.mentoview.service.dto.PaymentCheckDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortonePaymentService {

    private final WebClient webClient;
    private final PaymentService paymentService;

    @Value("${IMP_API_KEY}")
    private String portoneApiSecret; // PortOne API 시크릿
    private String baseUrl = "https://api.portone.io/";

    public boolean checkPayment(PaymentCreate paymentCreate, Long userId) {

        String encodedPaymentId = URLEncoder.encode(paymentCreate.getData().getPaymentId(), StandardCharsets.UTF_8);

        PaymentCheckDto paymentCheckDto = webClient.get()
                .uri(baseUrl + "payments/" + encodedPaymentId)
                .headers(headers -> headers.set("Authorization", "PortOne " + portoneApiSecret))
                .retrieve()
                .bodyToMono(PaymentCheckDto.class)
                .block(); // 동기 방식 호출 (비동기 필요 시 .subscribe() 사용)

        // 검증 로직 분리
        validatePayment(paymentCheckDto, userId);
        return true;
    }

    private void validatePayment(PaymentCheckDto paymentCheckDto, Long userId) {
        if (paymentCheckDto == null) {
            throw new RuntimeException("Invalid payment response");
        }

        // 결제 금액 및 상태 확인
        if (paymentCheckDto.getAmount().getTotal().compareTo(BigDecimal.valueOf(10000.0)) == 0 &&
                "PAID".equals(paymentCheckDto.getStatus())) {
            paymentService.createPayment(paymentCheckDto, userId);
        } else {
            throw new RuntimeException("Payment amount mismatch detected");
        }
    }

    public void schedulePayment(String billingKey) throws JsonProcessingException {

        String paymentId = "payment-" + UUID.randomUUID().toString();
        String encodedPaymentId = URLEncoder.encode(paymentId, StandardCharsets.UTF_8);

        String response = webClient.post()
                .uri(baseUrl + "payments/{paymentId}/schedule", encodedPaymentId)
                .header(HttpHeaders.AUTHORIZATION, "PortOne " + portoneApiSecret)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(createRequestBody(billingKey))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.out.println("결제 예약 요청 실패: " + error.getMessage()))
                .block();

        System.out.println(response);
    }

    public static String createRequestBody(String billingKey) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("billingKey", billingKey);
        paymentDetails.put("orderName", "월간 이용권 정기결제");

//        Map<String, Object> customer = new HashMap<>();
//        customer.put("id", customerId);
//        paymentDetails.put("customer", customer);

        Map<String, Integer> amount = new HashMap<>();
        amount.put("total", 10000);
        paymentDetails.put("amount", amount);

        paymentDetails.put("currency", "KRW");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("payment", paymentDetails);
        requestBody.put("timeToPay", "2025-02-28T01:55:00.4525493+09:00");
        // ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        //                                                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        String response = objectMapper.writeValueAsString(requestBody);
        System.out.println(response);

        return response;
    }

    public void checkBillingKey(String billingKey) {

        String encodedBillingKey = URLEncoder.encode(billingKey, StandardCharsets.UTF_8);

        BillingKeyCheckDto response = webClient.get()
                .uri(baseUrl + "billing-keys/{billingKey}", encodedBillingKey)
                .headers(headers -> headers.set("Authorization", "PortOne " + portoneApiSecret))
                .retrieve()
                .bodyToMono(BillingKeyCheckDto.class)
                .block(); // 동기 방식 호출 (비동기 필요 시 .subscribe() 사용)

        System.out.println(response);
        // 검증 로직 분리
    }

    public void createPayment(Long uId) {

        // 포트원 서버로 빌링키 결제 요청 보내고, 결제 예약

        // paymentId 생성
        String paymentId = "payment-" + UUID.randomUUID().toString();
        String encodedPaymentId = URLEncoder.encode(paymentId, StandardCharsets.UTF_8);

        // uid로 billingKey 조회
        String billingKey = "";


//        String response = webClient.post()
//                .uri(baseUrl + "payments/{paymentId}/billing-key", encodedPaymentId)
//                .header(HttpHeaders.AUTHORIZATION, "PortOne " + portoneApiSecret)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .bodyValue(createRequestBody(billingKey))
//                .retrieve()
//                .bodyToMono(String.class)
//                .doOnError(error -> System.out.println("결제 예약 요청 실패: " + error.getMessage()))
//                .block();

    }
}
