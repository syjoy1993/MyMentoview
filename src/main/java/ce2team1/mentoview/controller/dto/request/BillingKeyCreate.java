package ce2team1.mentoview.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingKeyCreate {
    @NotNull
    private String type; // 웹훅 타입
    @NotNull
    private String timestamp; // 시간
    @NotNull
    private Data data;

    @Getter
    public static class Data {

        private String billingKey;
        private String storeId;
    }
}
