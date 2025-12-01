package ce2team1.mentoview.user.presentation.dto.request;


import ce2team1.mentoview.user.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
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
public class UserMypage {

    @NotBlank
    private String password;


}