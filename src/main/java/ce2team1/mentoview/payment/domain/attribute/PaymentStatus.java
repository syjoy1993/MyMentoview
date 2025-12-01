package ce2team1.mentoview.payment.domain.attribute;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PaymentStatus {
    SUCCESS, // ("결제 성공"),
    FAILED, //("결제 실패"),
    PENDING; //("결제 대기");

}
