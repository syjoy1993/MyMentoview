package ce2team1.mentoview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebhookService {
    @Value("${PORTONE_WEBHOOK_SECRET}")
    private String portoneWebhookSecret; //PortOne Webhook 시크릿

    public <T> boolean checkWebhook(T payload, Map<String, String> headers) throws JsonProcessingException {

        // WebhookVerifier 선언
        WebhookVerifier webhookVerifier = new WebhookVerifier(portoneWebhookSecret);

        // payload를 PaymentCreate 객체가 아닌 json 형식의 String으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(payload);
        System.out.println(json);

        try {
            Webhook webhook = webhookVerifier.verify(
                    json,
                    headers.get("webhook-id"), // Webhook ID 헤더 값
                    headers.get("webhook-signature"), // Webhook Signature 헤더 값
                    headers.get("webhook-timestamp") // Webhook Timestamp 헤더 값
            );

            System.out.println("Webhook 검증 성공: " + webhook);
            return true;
        } catch (Exception e) {
            System.out.println("Webhook 검증 실패: " + e.getMessage());
            return false;
        }
    }
}
