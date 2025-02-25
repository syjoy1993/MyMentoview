package ce2team1.mentoview.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link ce2team1.mentoview.entity.User}.......???
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRead {
    private String email;
    private String profile;
    private String openid;

}