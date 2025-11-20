package ce2team1.mentoview.controller.dto.request;

import ce2team1.mentoview.payment.infra.portone.dto.PortoneCustomer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BillingKeyCreate {
    @NotNull
    private String type; // 웹훅 타입
    @NotNull
    private String timestamp; // 시간
    @NotNull
    private Data data; // 실제 data

    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String billingKey; // 발급된 빌링키
        private String storeId; // 상점 id
        private String channelId; // PG사 구분용 채널id
        private String issueName; // 주문명
        private String issueId; // 발급 식별자
        //결제수단
        private String billingKeyMethod; // "CARD", "EASY_PAY", "MOBILE"
        //고객 정보
        private PortoneCustomer customer ;
    }

}
