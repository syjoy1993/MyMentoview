package ce2team1.mentoview.config;

import ce2team1.mentoview.payment.infra.portone.PortoneApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.portone.sdk.server.webhook.WebhookVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PaymentConfig {

    @Value("${PORTONE_WEBHOOK_SECRET}")
    private String portoneWebhookSecret; //PortOne Webhook 시크릿

    @Value("${IMP_API_KEY}")
    private String portoneApiSecret; // PortOne API 시크릿

    @Value("${NOTIFICATION_URL}")
    private String notificationUrl; // 포트원이 웹훅 전달할 URL

    @Bean // 생성자 주입
    public WebhookVerifier webhookVerifier() {
        if (portoneWebhookSecret == null || portoneWebhookSecret.isBlank()) {
            throw new IllegalArgumentException("PORTONE_WEBHOOK_SECRET is required");
        }
        return new WebhookVerifier(portoneWebhookSecret);
    }

    @Bean
    public PortoneApiClient portoneApiClient(WebClient webClient, ObjectMapper objectMapper) {
        if (portoneApiSecret == null || portoneApiSecret.isBlank()) {
            throw new IllegalArgumentException("IMP_API_KEY is required");
        }
        return new PortoneApiClient(webClient, objectMapper, portoneApiSecret , notificationUrl);
    }

}
