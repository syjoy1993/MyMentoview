package ce2team1.mentoview.security.dto;

import ce2team1.mentoview.entity.atrribute.SocialProvider;

public interface OAuth2ResponseSocial {
    SocialProvider getProvider();
    String getProviderId();
    String getEmail();
    String getName();
    boolean isEmailVerified();
}
