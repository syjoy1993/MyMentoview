package ce2team1.mentoview.subscription.domain.attribute;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SubscriptionStatus {
    FREE_TRIAL, //("Free Trial")
    ACTIVE, //("활성"),
    CANCELED, // ("취소")
    PAUSED, // ("일시 정지")
    EXPIRY; // ("만료")


}
