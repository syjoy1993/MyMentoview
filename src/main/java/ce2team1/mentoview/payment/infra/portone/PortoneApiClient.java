package ce2team1.mentoview.payment.infra.portone;

import ce2team1.mentoview.exception.SubscriptionException;
import ce2team1.mentoview.payment.infra.portone.dto.PortoneBillingKey;
import ce2team1.mentoview.payment.application.dto.PortonePayment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.timeout.TimeoutException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Portone HTTP API 호출(WebClient)
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class PortoneApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiSecret;
    private final String notificationUrl;

    private final String baseUrl = "https://api.portone.io";
    private static final String AUTH_HEADER_PREFIX = "PortOne ";
    /*
     * Note
     *  - PortonePaymentService가 오케스트레이션을 담당 ->  PortoneApiClient는 PortonePaymentService의 API 통신 전담
     *  -
     * Todo
     *  - 예외처리 통일성 체크 필요
     *  - getPayment() 처럼 각 API마다 개별적인 예외처리가 필요하다
     *      - getPayment(String paymentId) V
     *      - getBillingKey(String billingKey)
     *      - createPayment()
     *      - schedulePayment()
     *      - cancelSchedules()
     *      - deleteBillingKey()
     *      - getScheduleTimeToPay()
     *
     * */


    // 결제 단건 조회
    public PortonePayment getPayment(String paymentId){
        String encodedPaymentId = URLEncoder.encode(paymentId, StandardCharsets.UTF_8);

        return webClient.get()
                .uri(baseUrl + "/payments/{paymentId}" + encodedPaymentId)
                .headers(headers ->headers.set(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + apiSecret))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.bodyToMono(String.class)
                                .map(body -> new SubscriptionException(
                                        "Portone 4xx 응답: " + body,
                                        HttpStatus.BAD_REQUEST))
                )
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        resp.bodyToMono(String.class)
                                .map(body -> new SubscriptionException(
                                        "Portone 5xx 응답: " + body,
                                        HttpStatus.BAD_GATEWAY))
                )
                .bodyToMono(PortonePayment.class)
                .timeout(Duration.ofSeconds(2))
                .retryWhen(
                        Retry.fixedDelay(2, Duration.ofMillis(200)) // 최대 2회 재시도
                                .filter(ex -> ex instanceof IOException
                                        || ex instanceof TimeoutException)
                                .onRetryExhaustedThrow((spec, signal) ->
                                        new SubscriptionException("Portone 결제 조회 재시도 초과", HttpStatus.GATEWAY_TIMEOUT))
                )
                .block();
    }

    //빌링키 단건조회
    public PortoneBillingKey getBillingKey(String billingKey) {

        String encodedBillingKey = URLEncoder.encode(billingKey, StandardCharsets.UTF_8);
        return webClient.get()
                .uri(baseUrl + "/billing-keys/{billingKey}" + encodedBillingKey)
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + apiSecret)
                .retrieve()
                .bodyToMono(PortoneBillingKey.class)
                .block();
    }

    //빌링키 결제 요청 (즉시 결제)
    public void createPayment(String paymentId, Long userId, String billingKey, String orderName, int amount) {
        String encodedPaymentId = URLEncoder.encode(paymentId, StandardCharsets.UTF_8);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("billingKey", billingKey);
        requestBody.put("orderName", orderName);
        requestBody.put("customer", Map.of("id", String.valueOf(userId)));
        requestBody.put("amount", Map.of("total", amount));
        requestBody.put("currency", "KRW");
        requestBody.put("noticeUrls", Collections.singletonList(notificationUrl));

        webClient.post()
                .uri(baseUrl + "/payments/{paymentId}/billing-key", encodedPaymentId)
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + apiSecret)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
    //결제 예약 (Schedule Payment)
    // 리턴값: Schedule ID (String)
    public String schedulePayment(String paymentId, Long userId, String billingKey, OffsetDateTime timeToPay) {
        String encodedPaymentId = URLEncoder.encode(paymentId, StandardCharsets.UTF_8);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> paymentDetails = new HashMap<>();

        paymentDetails.put("billingKey", billingKey);
        paymentDetails.put("orderName", "월간 이용권 정기결제");
        paymentDetails.put("customer", Map.of("id", String.valueOf(userId)));
        paymentDetails.put("amount", Map.of("total", 10000));
        paymentDetails.put("currency", "KRW");
        paymentDetails.put("noticeUrls", Collections.singletonList(notificationUrl));

        requestBody.put("payment", paymentDetails);
        requestBody.put("timeToPay", timeToPay.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        String response = webClient.post()
                .uri(baseUrl + "/payments/{paymentId}/schedule", encodedPaymentId)
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + apiSecret)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 응답 JSON에서 schedule.id 추출
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("schedule").path("id").asText();
        } catch (Exception e) {
            log.error("Schedule Response Parsing Error", e);
            return null;
        }
    }

    public void cancelSchedules(String billingKey){
        Map<String, String> requestBody = Map.of("billingKey", billingKey);

        webClient.method(HttpMethod.DELETE)
                .uri(baseUrl + "/payment-schedules")
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + apiSecret)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    //빌링키 삭제
    public void deleteBillingKey(String billingKey){
        String encodedBillingKey = URLEncoder.encode(billingKey, StandardCharsets.UTF_8);

        webClient.delete()
                .uri(baseUrl + "/billing-keys/" + encodedBillingKey)
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + apiSecret)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
    //예약 상세 조회 (timeToPay 확인용)
    public String getScheduleTimeToPay(String scheduleId) {
        String encodedScheduleId = URLEncoder.encode(scheduleId, StandardCharsets.UTF_8);

        String response = webClient.get()
                .uri(baseUrl + "/payment-schedules/" + encodedScheduleId)
                .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + apiSecret)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.path("timeToPay").asText();
        } catch (Exception e) {
            log.error("Get Schedule Error", e);
            return null;
        }
    }
}
