package ce2team1.mentoview.controller.dto.response;


import ce2team1.mentoview.service.dto.UserDto;
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
public class FormUserResp {
    private Long userId;
    private String email;
    private String name;
    private String role;
    private String status;
    private String message;

    public static FormUserResp of(UserDto userDto,String message) {
        FormUserRespBuilder formUserResp = FormUserResp.builder()
                .userId(userDto.getUserId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .role(userDto.getRole().getCode())
                .status(userDto.getStatus().toString());

        if (message != null) {
            formUserResp.message(message);  //message가 있을 때만
        }
        return formUserResp.build();
    }

}