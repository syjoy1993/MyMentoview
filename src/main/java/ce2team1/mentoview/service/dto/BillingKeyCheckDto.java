package ce2team1.mentoview.service.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BillingKeyCheckDto {
    private String status;
    private String billingKey;
    private String merchantId;
    private String storeId;
    private List<Channel> channels;
    private String issuedAt;
    private String deletedAt;

    @Getter
    public static class Channel{
        private String type;
        private String pgProvider;
        private String pgMerchatId;
    }

}
