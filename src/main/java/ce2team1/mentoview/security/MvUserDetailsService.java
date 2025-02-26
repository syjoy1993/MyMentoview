package ce2team1.mentoview.security;

import ce2team1.mentoview.repository.UserRepository;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import ce2team1.mentoview.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MvUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> new MvPrincipalDetails(UserDto.toDto(user), LoginType.FORM))
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
    }
}