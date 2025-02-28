package ce2team1.mentoview.service.dto;


import ce2team1.mentoview.entity.User;
import ce2team1.mentoview.entity.atrribute.Role;
import ce2team1.mentoview.entity.atrribute.SocialProvider;
import ce2team1.mentoview.entity.atrribute.UserStatus;
import lombok.*;

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
                .role(role)
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




}