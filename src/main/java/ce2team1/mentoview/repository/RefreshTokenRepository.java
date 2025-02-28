package ce2team1.mentoview.repository;

import ce2team1.mentoview.security.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Boolean existsByRefreshToken(String refreshToken);

  @Transactional
  void deleteByRefreshToken(String refreshToken);

  Optional<RefreshToken> findByUserEmail(String email);
}