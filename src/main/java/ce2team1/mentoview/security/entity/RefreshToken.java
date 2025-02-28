package ce2team1.mentoview.security.entity;

import ce2team1.mentoview.security.dto.RefreshTokenDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userEmail;
    private String refreshToken;
    private LocalDateTime expirationDate;

    public static RefreshToken of(String email, String refreshToken, LocalDateTime expirationDate) {
        return new RefreshToken(
                null,
                email,
                refreshToken,
                expirationDate
        );
    }
    public static RefreshToken of(Long id, String email, String refreshToken, LocalDateTime expirationDate) {
        return new RefreshToken(
                id,
                email,
                refreshToken,
                expirationDate
        );
    }
    public static RefreshToken toEntity(RefreshTokenDto tokenDto) {
        return RefreshToken.builder()
                .userEmail(tokenDto.getUserEmail())
                .refreshToken(tokenDto.getRefreshToken())
                .expirationDate(tokenDto.getExpirationDate())
                .build();

    }



}
