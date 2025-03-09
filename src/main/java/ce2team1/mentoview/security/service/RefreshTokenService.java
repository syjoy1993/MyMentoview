package ce2team1.mentoview.security.service;

import ce2team1.mentoview.repository.RefreshTokenRepository;
import ce2team1.mentoview.security.dto.RefreshTokenDto;
import ce2team1.mentoview.security.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = false)
    public RefreshTokenDto addRefreshToken(String userEmail, String refreshToken, Long expiration) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiration), ZoneId.systemDefault());
        RefreshTokenDto tokenDto = RefreshTokenDto.of(userEmail, refreshToken, dateTime);
        refreshTokenRepository.save(RefreshToken.toEntity(tokenDto));
        return tokenDto;

    }

    public String getRefreshToken(String email) {
        return refreshTokenRepository.findByUserEmail(email).map(
                refreshToken -> refreshToken.getRefreshToken()).orElse(null);
    }

}
