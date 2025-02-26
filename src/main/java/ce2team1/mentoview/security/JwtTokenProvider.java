package ce2team1.mentoview.security;

import ce2team1.mentoview.entity.atrribute.Role;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/*
* Jwt : 0.12.x버전
 * */
@Getter
@Component
public class JwtTokenProvider {

    private SecretKey secretKey;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;

    //JwtTokenProvider 생성자
    public JwtTokenProvider(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${access-token-expiration}") long accessTokenExpiration,
            @Value("${refresh-token-expiration}") long refreshTokenExpiration)
     {
        this.secretKey  = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    //AccessToken
    public String createAccessToken(String email, Role role) {
        return createToken(email, role, accessTokenExpiration);
    }
    //RefreshToken
    public String createRefreshToken(String email, Role role) {
        return createToken(email, role, refreshTokenExpiration);
    }

    // 토큰 검증 메서드 : 토큰을 인자로 받음 ,java꺼
    public String getEmailFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("email").toString();
    }
    public String getRoleFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("role").toString();
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .getExpiration().before(new Date());
    }

    // 토큰 생성!
    private  String createToken(String email, Role role,  Long expiration) {
        return Jwts.builder()
                .claim("email", email)
                .claim("role", role.getCode())                        // 이거 체크해라 .toString()저
                .issuedAt(new Date(System.currentTimeMillis())) //시간
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    /*public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }*/

}
