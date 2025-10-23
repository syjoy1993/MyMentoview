package ce2team1.mentoview.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PaymentCreate {

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
