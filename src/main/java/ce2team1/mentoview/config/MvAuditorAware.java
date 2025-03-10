package ce2team1.mentoview.config;

import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component("MvAuditorAware")
public class MvAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("anonymousUser"); // 인증되지 않은 경우 anonymousUser
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof MvPrincipalDetails) { //폼 로그인 사용자
            return Optional.of(((MvPrincipalDetails) principal).getUsername());
        } else if (principal instanceof OAuth2User) { // OAuth 로그인 사용자
            Map<String, Object> attributes = ((OAuth2User) principal).getAttributes();
            return Optional.ofNullable((String) attributes.get("email"));
        }

        return Optional.of("anonymousUser"); //
    }
}