package ce2team1.mentoview.controller.dto.request;


import ce2team1.mentoview.entity.atrribute.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
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
public class UserCreateForm {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String name;

    @JsonIgnore
    private boolean isSocial = false;
    private UserStatus status = UserStatus.ACTIVE;



}