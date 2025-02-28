package ce2team1.mentoview.controller.dto.response;


import ce2team1.mentoview.entity.atrribute.Role;
import ce2team1.mentoview.entity.atrribute.SocialProvider;
import ce2team1.mentoview.entity.atrribute.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ce2team1.mentoview.entity.User}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OathUserResp {
    Long userId;
    String email;
    String name;
    Role role;
    SocialProvider socialProvider;
    String providerId;
    UserStatus status;
}