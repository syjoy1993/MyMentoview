package ce2team1.mentoview.controller.dto.response;


import ce2team1.mentoview.entity.atrribute.Role;
import ce2team1.mentoview.entity.atrribute.SocialProvider;
import ce2team1.mentoview.entity.atrribute.UserStatus;
import ce2team1.mentoview.service.dto.UserDto;
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
public class UserResp {
    private Long userId;
    private String email;
    private String name;
    private Role role;
    private SocialProvider socialProvider;
    private String providerId;
    private UserStatus status;
    private String message;

    public static UserResp of(UserDto userDto, String message) {
        return UserResp.builder()
                .userId(userDto.getUserId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .role(userDto.getRole())
                .status(userDto.getStatus())
                .message(message)
                .build();
    }
}