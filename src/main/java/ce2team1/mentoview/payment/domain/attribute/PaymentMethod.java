package ce2team1.mentoview.payment.domain.attribute;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PaymentMethod {
    KAKAO_PAY, //("KAKAO PAY"),
    CREDIT_CARD; //("CARD");
}
