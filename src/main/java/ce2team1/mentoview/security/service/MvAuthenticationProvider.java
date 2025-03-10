package ce2team1.mentoview.security.service;

import ce2team1.mentoview.entity.User;
import ce2team1.mentoview.repository.UserRepository;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import ce2team1.mentoview.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class MvAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();

        User userByEmail = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("사용자 없음" + email));
        if (userByEmail.getPassword() == null || !passwordEncoder.matches(password, userByEmail.getPassword())) {
            throw new BadCredentialsException("비밀번호가 틀렸습니다.");
        }

        UserDto userDto = UserDto.toForm(userByEmail);
        MvPrincipalDetails mvPrincipalDetails = MvPrincipalDetails.of(userDto);

        return new UsernamePasswordAuthenticationToken(mvPrincipalDetails, null, mvPrincipalDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
