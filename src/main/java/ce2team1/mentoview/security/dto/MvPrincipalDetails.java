package ce2team1.mentoview.security.dto;

import ce2team1.mentoview.entity.atrribute.UserStatus;
import ce2team1.mentoview.security.LoginType;
import ce2team1.mentoview.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//Provider가 받아서 로그인 한다
@RequiredArgsConstructor
public class MvPrincipalDetails implements OAuth2User, UserDetails{

    private final UserDto userDto;
    private final LoginType loginType;

    //검증 이메일로함
    @Override
    public String getName() {
        return userDto.getEmail();
    }

    @Override
    public String getPassword() {
        return userDto.getPassword() != null ? userDto.getPassword() : "";
    }

    public String getUsername() {
        return userDto.getName();
    }

    public Long getUserId() {
        return userDto.getUserId();
    }

    public LoginType getLoginType() {
        return loginType;
    }

    @Override
    public boolean isEnabled() {
        return userDto.getStatus() == UserStatus.ACTIVE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userDto.getRole().getCode();
            }
        });
        return collection;
    }

    @Override
    public Map<String, Object> getAttributes() { // 받은 데이터값
        return Map.of();
    }
}
