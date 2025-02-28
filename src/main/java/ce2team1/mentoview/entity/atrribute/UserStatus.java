package ce2team1.mentoview.entity.atrribute;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum UserStatus {
    ACTIVE, //("활성")
    INACTIVE, //("미인증")
    LOCKED, //("잠금")
    SUSPENDED, //("정지")
    DELETED; //("탈퇴")

}
