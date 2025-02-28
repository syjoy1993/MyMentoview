package ce2team1.mentoview.entity.atrribute;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BillingCycle {
    MONTHLY, //("월별"),
    YEARLY; //("연간");

}
