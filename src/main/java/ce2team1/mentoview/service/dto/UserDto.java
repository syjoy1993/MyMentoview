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
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDto {
    Long userId;
    String email;
    String password;
    String name;
    Role role;
    SocialProvider socialProvider;
    String socialId;
    boolean isSocial;
    UserStatus status;

    public static UserDto of(String email, String password, String name, Role role, SocialProvider socialProvider, String socialId, boolean isSocial, UserStatus status) {
        return new UserDto(null,email, password, name, role, socialProvider, socialId, isSocial, status);
    }
    public static UserDto of(Long userId,String email, String password, String name, Role role, SocialProvider socialProvider, String socialId, boolean isSocial, UserStatus status) {
        return new UserDto(userId, email, password, name, role, socialProvider, socialId, isSocial, status);
    }
    public UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .socialProvider(user.getSocialProvider())
                .socialId(user.getSocialId())
                .isSocial(user.isSocial())
                .status(user.getStatus())
                .build();
    }


}