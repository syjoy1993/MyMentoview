package ce2team1.mentoview.service.dto;


import ce2team1.mentoview.entity.User;
import ce2team1.mentoview.entity.atrribute.Role;
import ce2team1.mentoview.entity.atrribute.SocialProvider;
import ce2team1.mentoview.entity.atrribute.UserStatus;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import lombok.*;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Objects;

/**
 * DTO for {@link User}
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDto {
    private Long userId;
    private String email;
    private String password;
    private String name;
    private Role role;
    private SocialProvider socialProvider;
    private String providerId;
    private UserStatus status;
    private String billingKey;

    public static UserDto of(String email, String password, String name, Role role, SocialProvider socialProvider, String providerId, UserStatus status, String billingKey) {
        return new UserDto(null, email, password, name, role, socialProvider, providerId,  status, null);
    }
    public static UserDto of(Long userId,String email, String password, String name, Role role, SocialProvider socialProvider, String providerId, UserStatus status, String billingKey) {
        return new UserDto(userId, email, password, name, role, socialProvider, providerId, status, null);
    }
    public static UserDto of(String email, Role role) {
        return UserDto.builder()
                .email(email)
                .password(null)
                .role(role)
                .build();
    }
    public static UserDto ofForm(String email, String password, Role role) {
        Objects.requireNonNull(password, "폼 로그인 시 비밀번호는 필수입니다.");
        return UserDto.builder()
                .email(email)
                .password(password)
                .role(role)
                .build();
    }

    public UserDto updatePassword(String newPassword) {
        Objects.requireNonNull(newPassword, "새로운 비밀번호는 null이 될 수 없습니다.");
        return this.toBuilder()
                .password(newPassword)
                .build();
    }


    public static UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .socialProvider(user.getSocialProvider())
                .providerId(user.getProviderId())
                .status(user.getStatus())
                .build();
    }
    public static UserDto toForm(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .role(user.getRole())
                .socialProvider(user.getSocialProvider())
                .providerId(user.getProviderId())
                .status(user.getStatus())
                .build();
    }
    public static UserDto byOAuth2User(OAuth2User  oAuth2User) {
        String email = null;
        String name = null;


        if(oAuth2User instanceof OidcUser oidcUser) {
            Map<String, Object> claims = oidcUser.getClaims();
            email = (String) claims.getOrDefault("email", null);
            name = (String) claims.getOrDefault("name", null);
        }
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (email == null) {
            if(attributes.containsKey("email")) {
                email = (String) attributes.get("email");
            } else if (attributes.containsKey("mail")) {
                email = (String) attributes.get("mail");
            }
        }
        if(name == null) {
            if(attributes.containsKey("name")) {
                name = (String) attributes.get("name");
            } else if (attributes.containsKey("displayName")) {
                name = (String) attributes.get("displayName");
            }
        }
        return UserDto.builder()
                .email(email)
                .name(name)
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static UserDto byOAuth2User(MvPrincipalDetails principalDetails) {
        return principalDetails.getUserDto();
    }

}
