package ce2team1.mentoview.security;

import ce2team1.mentoview.entity.atrribute.Role;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@AllArgsConstructor
public class MvSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        MvPrincipalDetails mvPrincipalDetails = (MvPrincipalDetails) authentication.getPrincipal();

        String userEmail = mvPrincipalDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtTokenProvider.createAccessToken(userEmail,Role.toCode(role));//15분 //60 * 60 * 60L = 1시간
        String refreshToken = jwtTokenProvider.createRefreshToken(userEmail,Role.toCode(role));// 7일
            // 우리가 만든 refreshToken 디비로 저장
        refreshTokenService.addRefreshToken(userEmail, refreshToken, jwtTokenProvider.getRefreshTokenExpiration());

        response.addCookie(createCookie("Authorization", accessToken));
        response.sendRedirect("http://localhost:3000/"); // 도메인으로 변경시 아래도 주석해제, https풀기


    }

    private Cookie createCookie(String key, String value ) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
//      cookie.setSecure(true); https풀기
        cookie.setAttribute("SameSite", "None"); // 크로스 사이트 요청에서도 쿠키가 유지되도록 설정
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;

    }

}
