package ce2team1.mentoview.entity.atrribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

//컨버터 필요
@Getter
@ToString
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");
    private final String code;



}
