package ce2team1.mentoview.security;

import ce2team1.mentoview.entity.User;
import ce2team1.mentoview.entity.atrribute.Role;
import ce2team1.mentoview.entity.atrribute.UserStatus;
import ce2team1.mentoview.repository.UserRepository;
import ce2team1.mentoview.security.dto.GoogleOAuth2Response;
import ce2team1.mentoview.security.dto.MvOAuth2User;
import ce2team1.mentoview.security.dto.OAuth2ResponseSocial;
import ce2team1.mentoview.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MvOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("user: {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        //공급사 확인 , 추후 공급사 추가 예정 고려
        OAuth2ResponseSocial responseSocial = null;

        if (registrationId.equals("google")) {
            responseSocial = new GoogleOAuth2Response((oAuth2User.getAttributes()));
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }

        //소셜로그인 시도자
        UserDto userDto = UserDto.of(
                responseSocial.getEmail(),
                null,
                responseSocial.getName(),
                Role.USER,
                responseSocial.getProvider(), // 소셜 제공자
                responseSocial.getProviderId(), // 소셜 고유 ID
                true, // 소셜 로그인 여부
                UserStatus.ACTIVE
        );

        Optional<User> findByEmailSocialUser =
                userRepository.findByEmailAndProviderId((responseSocial.getEmail()), responseSocial.getProviderId());
        if (findByEmailSocialUser.isPresent()) {
            return new MvOAuth2User(userDto);
        } else {
            throw new OAuth2AuthenticationException("SOCIAL_LOGIN_NEW_USER");
        }

    }

}


