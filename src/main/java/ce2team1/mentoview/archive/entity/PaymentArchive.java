package ce2team1.mentoview.archive.entity;

import ce2team1.mentoview.entity.atrribute.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access =  AccessLevel.PRIVATE)
@ToString
public class PaymentArchive {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //복합 UNIQUE: user_id, approval_code
    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="approval_code", nullable=false, length=128)
    private String approvalCode; // 승인번호

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String subscription;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    protected LocalDateTime archivedAt; // 아카이브된 시간

    public static PaymentArchive of(Long userId, BigDecimal amount, String approvalCode, String subscription, PaymentStatus status, LocalDateTime paymentDate) {
        return PaymentArchive.builder()
                .userId(userId)
                .amount(amount)
                .approvalCode(approvalCode)
                .subscription(subscription)
                .status(status)
                .paymentDate(paymentDate)
                .archivedAt(LocalDateTime.now())
                .build();
    }
}
