package ce2team1.mentoview.security.dto;

import ce2team1.mentoview.entity.atrribute.SocialProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
@Getter
@Builder
@RequiredArgsConstructor
public class GoogleOAuth2Response implements OAuth2ResponseSocial {

    private final Map<String, Object> googleUserAttribute;


    @Override
    public SocialProvider getProvider() {

        return SocialProvider.byValue(googleUserAttribute.get("provider").toString());
    }

    @Override
    public String getProviderId() {
        return googleUserAttribute.get("providerId").toString();
    }

    @Override
    public String getEmail() {
        return googleUserAttribute.get("email").toString();
    }

    @Override
    public String getName() {
        return googleUserAttribute.get("name").toString();
    }

    @Override
    public boolean isEmailVerified() {
        return Boolean.TRUE.equals(googleUserAttribute.get("email_verified"));
        // 내부가 true 이면 true, false or null false반한
    }
}
