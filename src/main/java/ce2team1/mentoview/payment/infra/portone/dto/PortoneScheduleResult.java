package ce2team1.mentoview.payment.infra.portone.dto;

import java.time.OffsetDateTime;

public record PortoneScheduleResult(
        String paymentId,
        String scheduleId,
        OffsetDateTime timeToPay
) {
}
