package ce2team1.mentoview.security.dto;

import ce2team1.mentoview.entity.atrribute.SocialProvider;

import java.util.Map;

public interface OAuth2ResponseSocial {
    SocialProvider getProvider();
    String getProviderId();
    String getEmail();
    String getName();
    boolean isEmailVerified();

    default Map<String, Object> getUserAttribute() {
        return getUserAttribute();
    }

}
