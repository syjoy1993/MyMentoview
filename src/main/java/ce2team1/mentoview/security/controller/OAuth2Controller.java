package ce2team1.mentoview.security.controller;

import ce2team1.mentoview.controller.dto.response.UserResp;
import ce2team1.mentoview.exception.UserException;
import ce2team1.mentoview.security.JwtTokenProvider;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import ce2team1.mentoview.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "User API", description = "OAthu2")
@RequiredArgsConstructor
public class OAuth2Controller {
    private final UserService userService;
    //private final OAuthProperties oAuthProperties;
    private final JwtTokenProvider jwtTokenProvider;

/*

    @GetMapping("/google")
    public ResponseEntity<?> getGoogleOAuthUrl() {
        log.info("🔍 OAuthProperties의 redirectUri 값: {}", oAuthProperties.getRedirectUri());

        String redirectUri = oAuthProperties.getRedirectUri();
        if (redirectUri == null || redirectUri.isBlank()) {
            log.error("‼️ OAuthProperties에서 redirectUri 값이 누락됨");
            redirectUri = "http://localhost:8080/login/oauth2/code/google";
        }

        log.info("✅ 최종 Google OAuth Redirect URI : {}", redirectUri);

        String uriString = UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth?")
                .queryParam("client_id", oAuthProperties.getClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
                .toUriString();

        log.info("✅ 최종 Google OAuth Redirect URI : {}", uriString);


        return ResponseEntity.ok(Map.of("authUrl", uriString));
    }
*/



    @Operation(summary = "OAuth2", description = "OAuth2 Token && user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OAuth2 user"),
            @ApiResponse(responseCode = "400", description = "NOT_FOUND")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public ResponseEntity<UserResp> getUserInfo (@AuthenticationPrincipal MvPrincipalDetails mvPrincipalDetails,
                                             HttpServletRequest request) {
        if (mvPrincipalDetails == null || mvPrincipalDetails.getUserDto() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(UserResp.of(null, "인증되지 않은 사용 "));
        }

        log.info("mvPrincipalDetails: {}", mvPrincipalDetails);

        String existingToken = Optional.ofNullable(request.getCookies())
                .map(Arrays::stream)
                .flatMap(stream -> stream
                        .filter(cookie -> "auth-token".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElseThrow(() -> new UserException("인증 되지않은 사용자",HttpStatus.UNAUTHORIZED ));
        log.info("existingToken: {}", existingToken);
        //Arrays.stream(cookies).anyMatch(cookie -> "Authorization".equals(cookie.getName()));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + existingToken);

        log.info("headers: {}", headers);

        UserResp userResp = UserResp.of(mvPrincipalDetails.getUserDto(), "사용자 정보 조회 성공");

        log.info("userResp: {}", userResp);
        
        return ResponseEntity.ok().headers(headers).body(userResp);
    }

}
