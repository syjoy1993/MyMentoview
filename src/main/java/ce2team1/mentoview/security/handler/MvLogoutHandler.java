package ce2team1.mentoview.security.handler;

import ce2team1.mentoview.exception.ServiceException;
import ce2team1.mentoview.security.service.JwtTokenProvider;
import ce2team1.mentoview.security.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MvLogoutHandler implements LogoutHandler {
// Logout 멱등(항상 같은 결과) 정책도입
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        try {
            String authorization = request.getHeader("Authorization");
            String token = null;


            if (authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
                token = authorization.substring(7).trim();
                if (token.isEmpty()) {
                    token = null; // "Bearer"만 온 경우 방어
                }
            }
            if (token == null) {
                // extracted(response, HttpServletResponse.SC_BAD_REQUEST, "{\"code\":\"INVALID_REQUEST\",\"message\":\"토큰이 없습니다\"}");
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); //멱등 응답
                return;
            }

            String email = jwtTokenProvider.getEmailFromToken(token);
            refreshTokenService.deleteRefreshToken(email);
            //성공 응답은 204(또는 200)로 단순화
            //멱등 응답
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            //extracted(response, HttpServletResponse.SC_ACCEPTED, "{\"code\" : \"LOGOUT_SUCCESS\", \"message\" : \" 로그아웃 성공, Access Token 삭제 요청\"}");
            return;
        } catch (ServiceException e) {
            //extracted(response, HttpServletResponse.SC_BAD_REQUEST, "{\"code\":\"TOKEN_NOT_FOUND\", \"message\":\"삭제할 토큰이 없습니다.\"}");
            //서버 상태 정리 관점에선 실패도 204로 통일(멱등)
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        } catch (Exception e) {
            log.warn("Logout error: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;

        }finally {
            // [보강 8] 컨텍스트 정리
            SecurityContextHolder.clearContext();
        }
    }
/*    private void extracted(HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            log.error("{}",e.getMessage(),e);
        }
    }*/
}
