package ce2team1.mentoview.payment.infra.portone.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*
* Portone API 공통 Customer객체
* */
@Getter
@ToString
@NoArgsConstructor
public class PortoneCustomer {
    private String id;// 필수
    @ToString.Exclude
    private String phoneNumber;
    @ToString.Exclude
    private String email;
    @ToString.Exclude
    private String fullName;
}
