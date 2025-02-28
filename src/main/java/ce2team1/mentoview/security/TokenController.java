package ce2team1.mentoview.security;

import ce2team1.mentoview.entity.atrribute.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Access Token", description = "Access Token 증명 & 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급 완료"),
            @ApiResponse(responseCode = "400", description = "리프레시 토큰 없음"),
            @ApiResponse(responseCode = "400", description = "리프레시 토큰 만료"),
            @ApiResponse(responseCode = "500", description = "리프레시 증명되지 않음")
    })
    @PostMapping("/access")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String authHeader = request.getHeader("Authorization"); //토큰

        String emailFromToken = jwtTokenProvider.getEmailFromToken(authHeader);//email

        String refreshToken = refreshTokenService.getRefreshToken(emailFromToken); // refresh

        if (refreshToken == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtTokenProvider.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {

            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String type = jwtTokenProvider.getType(authHeader);
        if (!type.equals("access")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.FORBIDDEN);
        }

        Role roleFromToken = jwtTokenProvider.getRoleFromToken(refreshToken);
        String emailFromRefresh = jwtTokenProvider.getEmailFromToken(refreshToken);

        String accessToken = jwtTokenProvider.createAccessToken(emailFromRefresh, roleFromToken); //엑세스
        response.setHeader("Authorization", accessToken);

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
