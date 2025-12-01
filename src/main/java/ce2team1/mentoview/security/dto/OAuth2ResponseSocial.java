package ce2team1.mentoview.security.dto;

import ce2team1.mentoview.user.domain.entity.atrribute.SocialProvider;

import java.util.Map;

public interface OAuth2ResponseSocial {
    SocialProvider getProvider();
    String getProviderId(); // 사용자 유일 식별자 최대 255
    String getEmail();
    String getName();
    boolean isEmailVerified();

    default Map<String, Object> getUserAttribute() {
        return getUserAttribute();
    }

}
