package ce2team1.mentoview.payment.infra.portone;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


@RequiredArgsConstructor
@Slf4j
@Component
public class WebhookVerifierAdapter {
    //WebhookService -> 클래스명 +용도 변경 WebhookVerifierAdapter

    /*
    * todo
    *   - WebhookVerifier.verify() : Webhook 객체를 생성하고 유효성을 검증하는 메서드
    *       -> 문서 상 내부에 parsing 후 매핑해줌 ObjectMapper에 담아서 사용X
    *   -   이유:
    *       - ObjectMapper으로 parsing : 파싱하면서  body 소멸
    *       - 가장 큰문제는 raw data가 아니므로,
    *        실제 WebhookVerifier가 parsing 할때 이미 다른 데이터가 되어 해시함수가 다른 결과를 낼 가능 성이 높음
    *   ==> 방향 변경 : WebhookController에서 모든 비즈니스 로직을 담당하고 있음이 실질적 비즈니스 로직담당
    *   -> WebhookService -> 네이밍 변경 WebhookVerifierAdapter WebhookVerifier을 담는 역할로 사용
    */
    private WebhookVerifier webhookVerifier; // Bean등록

    public boolean verifyWebhook(String rawBody, Map<String, String> headers) throws JsonProcessingException {

        try {
            Webhook webhook = webhookVerifier.verify(
                    rawBody,
                    headers.get("webhook-id"), // Webhook ID 헤더 값
                    headers.get("webhook-signature"), // Webhook Signature 헤더 값
                    headers.get("webhook-timestamp") // Webhook Timestamp 헤더 값
            );

            log.info("Webhook 검증 성공: Id={}, TimeStamp={}", headers.get("webhook-id"), headers.get("webhook-timestamp"));
            return true;
        } catch (Exception e) {
            // 서명 불일치,유효 만료
            log.error("Webhook 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}
