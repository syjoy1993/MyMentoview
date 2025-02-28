package ce2team1.mentoview.security.dto;

import ce2team1.mentoview.security.entity.RefreshToken;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for {@link ce2team1.mentoview.security.entity.RefreshToken}
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenDto {
    Long id;
    String userEmail;
    String refreshToken;
    LocalDateTime expirationDate;


    public static RefreshTokenDto of(String userEmail, String refreshToken, LocalDateTime expirationDate) {
        return new RefreshTokenDto(
                null,
                userEmail,
                refreshToken,
                expirationDate
        );
    }

    public static RefreshTokenDto toDto(RefreshToken refreshToken) {
        return RefreshTokenDto.builder()
                .id(refreshToken.getId())
                .userEmail(refreshToken.getUserEmail())
                .refreshToken(refreshToken.getRefreshToken())
                .expirationDate(refreshToken.getExpirationDate())
                .build();
    }
}