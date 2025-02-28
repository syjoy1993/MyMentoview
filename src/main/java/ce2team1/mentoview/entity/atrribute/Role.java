package ce2team1.mentoview.entity.atrribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

//컨버터 필요
@Getter
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String code;

    public static Role toCode(String code) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 역할: " + code));
    }

}
