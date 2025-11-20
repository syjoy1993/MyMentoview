package ce2team1.mentoview.service.dto;

import ce2team1.mentoview.payment.infra.portone.dto.PortoneBillingKey;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillingKeyDto {

    private Long id;
    private String billingKey;
    private String status;
    private String pgProvider;
    private LocalDateTime issuedDate;
    private Boolean chosen;
    private Long uId;

    private static BillingKeyDto of (String billingKey, String status, String pgProvider, LocalDateTime issuedDate, Boolean chosen, Long uId) {
        return new BillingKeyDto(null, billingKey, status, pgProvider, issuedDate, chosen, uId);
    }
    private static BillingKeyDto of (Long id, String billingKey, String status, String pgProvider, LocalDateTime issuedDate, Boolean chosen, Long uId) {
        return new BillingKeyDto(id, billingKey, status, pgProvider, issuedDate, chosen, uId);
    }

    public static BillingKeyDto fromPortone(PortoneBillingKey portoneBillingKey) {

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(portoneBillingKey.getIssuedAt(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.systemDefault());

        String pgProvider = "UNKNOWN";
        if (portoneBillingKey.getChannels() != null && !portoneBillingKey.getChannels().isEmpty()) {
            pgProvider = portoneBillingKey.getChannels().get(0).getPgProvider();
        }

        return BillingKeyDto.of(
                portoneBillingKey.getBillingKey(),
                portoneBillingKey.getStatus(),
                portoneBillingKey.getChannels().get(0).getPgProvider(),
                localDateTime,
                true,
                Long.valueOf(portoneBillingKey.getCustomer().getId()));
    }

}
