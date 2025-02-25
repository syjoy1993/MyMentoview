package ce2team1.mentoview.security.dto;

import ce2team1.mentoview.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//Provider가 받아서 로그인 한다
@RequiredArgsConstructor
public class MvOAuth2User implements OAuth2User {

    private final UserDto userDto;

    @Override
    public String getName() {
        return userDto.getEmail();
    }//검증

    @Override
    public Map<String, Object> getAttributes() { // 받은 데이터값
        return Map.of();
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

    public String getUsername() {
        return userDto.getName();
    }
    public Long getUserId() {
        return userDto.getUserId();
    }
}
