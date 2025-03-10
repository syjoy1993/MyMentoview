package ce2team1.mentoview.controller.dto.request;


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

    @Builder.Default
    private boolean isSocial = false;



}