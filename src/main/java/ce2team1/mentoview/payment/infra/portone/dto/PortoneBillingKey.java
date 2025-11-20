package ce2team1.mentoview.payment.infra.portone.dto;

import lombok.Getter;

import java.util.List;
/*
* todo
*   - 네이밍 변경 필요 BillingKeyCheckDto -> PortoneBillingKey
*   - 실제 하는 일 -> BillingKey 단건 조회 API 응답
*   - GET /billing-keys/{billingKey}
*   - PortoneBillingKeyResp 변경
* - Portone 문서 기준 주요 필드
*       - billingKey
*       - merchantId
*       - storeId
*       - channels
*       - customer
*       - issuedAt
*       - deletedAt
*
*/


@Getter
public class PortoneBillingKey {
    private String status;                  //"ISSUED", "DELETED" 등 빌링키 상태
    private String billingKey;             // 빌링키 값
    private String merchantId;            //고객사/상점 아이디
    private String storeId;
    private List<Channel> channels;       // channels[0].pgProvider 발급된 PG/채널 정보
    private PortoneCustomer customer; // 우리 쪽 userId로 사용하는 고객 식별자
    private String issuedAt;              // 발급 시점 (RFC3339)
    private String deletedAt;             // 삭제 시점 (DELETED 상태일 때)

    @Getter
    public static class Channel{
        private String type;
        private String pgProvider;
        private String pgMerchatId;
    }


}
