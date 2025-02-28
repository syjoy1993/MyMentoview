package ce2team1.mentoview.entity.atrribute;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SocialProvider {
    NONE("none"),
    GOOGLE("google");

    private final String value;
    public static SocialProvider byValue(String provider) {
        switch (provider) {
            case "" :
                return SocialProvider.NONE;
            case "google":
                return SocialProvider.GOOGLE;

                default: throw new OAuth2AuthenticationException("지원하지 않는 소셜 제공자입니다: " + provider);

        }

    }
}
