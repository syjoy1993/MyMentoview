package ce2team1.mentoview.payment.domain.entity;


import ce2team1.mentoview.payment.domain.attribute.PaymentStatus;
import ce2team1.mentoview.subscription.domain.entity.Subscription;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Payment {
    /*
     * todo : Payment, Subscription
     *  - ERD에 맞게 Column 타입 설정 V
     *  - 필드명 변경 + 반영 V
     *  - 생성자 메서드 변경완료 -> Service 계층 Dto까지 변경 V
     *
     * */


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "pg_approval_code", nullable = false)
    private String pgApprovalCode; //pg_tid

    @Column(name= "transaction_id", unique = true,nullable = false)
    private String transactionId; //Portone의 paymentId, imp_uid

    @Column(name= "merchant_uid",nullable = false)
    private String merchantUid;// 우리가 만든 주문번호 (paymentId)


    @Column(nullable = false, columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    public static Payment of(BigDecimal amount, String pgApprovalCode, PaymentStatus status,
                             LocalDateTime paymentDate, Subscription subscription,
                             String transactionId, String merchantUid) {
        return Payment.builder()
                .amount(amount)
                .pgApprovalCode(pgApprovalCode)
                .status(status)
                .paymentDate(paymentDate)
                .subscription(subscription)
                .transactionId(transactionId)
                .merchantUid(merchantUid)
                .build();
    }
    public static Payment of(Long paymentId,BigDecimal amount, String pgApprovalCode, PaymentStatus status,
                             LocalDateTime paymentDate, Subscription subscription,
                             String transactionId, String merchantUid) {
        return Payment.builder()
                .paymentId(paymentId)
                .amount(amount)
                .pgApprovalCode(pgApprovalCode)
                .status(status)
                .paymentDate(paymentDate)
                .subscription(subscription)
                .transactionId(transactionId)
                .merchantUid(merchantUid)
                .build();
    }
/*    public static Payment toPayment(PortonePayment dto, Subscription subscription) {
        return Payment.builder()
                .amount(dto.getAmount().getTotal())
                .pgApprovalCode(dto.getTransactionId() != null ? dto.getTransactionId() : dto.getId())
                .status(PaymentStatus.SUCCESS)
                .paymentDate(LocalDateTime.now()) // 실제로는 dto.getPaidAt() 파싱 권장
                .subscription(subscription)
                .transactionId(dto.getId())       // 포트원 ID
                .merchantUid(dto.getMerchantId()) // 주문번호
                .build();
    }*/

    /*
    * todo 완료
    *   - private String  portone_transaction_id ->transactionId
    *   - private String  order_id_portone_ob -> merchantUid
    *   - private String pg_tid -> pgApprovalCode
    *   - status
    *       - @Enumerated(EnumType.STRING) hibernate6.xx && MySQL정책 변경 반영
    *       => @Column(nullable = false, columnDefinition = "varchar(20)") 추가
    */



    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Payment payment = (Payment) o;
        return getPaymentId() != null && Objects.equals(getPaymentId(), payment.getPaymentId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
