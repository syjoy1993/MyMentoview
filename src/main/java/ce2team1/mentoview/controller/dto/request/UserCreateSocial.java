package ce2team1.mentoview.controller.dto.request;


import ce2team1.mentoview.entity.atrribute.SocialProvider;
import ce2team1.mentoview.entity.atrribute.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UserCreateSocial {

    private Long userId;

    @NotBlank
    private String email;
    @NotBlank
    private String name;

    @NotNull
    private SocialProvider socialProvider;
    @NotBlank
    private String socialId;
    @Builder.Default
    private boolean isSocial = true;
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

}