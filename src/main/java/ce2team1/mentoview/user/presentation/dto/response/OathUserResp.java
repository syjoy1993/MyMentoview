package ce2team1.mentoview.user.presentation.dto.response;


import ce2team1.mentoview.user.domain.entity.User;
import ce2team1.mentoview.user.domain.entity.atrribute.Role;
import ce2team1.mentoview.user.domain.entity.atrribute.SocialProvider;
import ce2team1.mentoview.user.domain.entity.atrribute.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link User}
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