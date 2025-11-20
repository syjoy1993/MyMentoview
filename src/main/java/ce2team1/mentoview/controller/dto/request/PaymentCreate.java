package ce2team1.mentoview.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PaymentCreate {
    /*
     * todo
     *  - PaymentCreate, BillingKeycreate -> controller.dto.request 패키지 미사용 객체 정리 -> 사용후 패키지 명에 맞게 이동
     *  -
     *  -
     *
     * */


    @NotNull
    private String type; // 웹훅 타입
    @NotNull
    private String timestamp; // 시간
    @NotNull
    private Data data;

    @Getter
    public static class Data {
        private String transactionId;
        private String paymentId;
        private String storeId;

    }

}
