package ce2team1.mentoview.user.domain.repository;


import ce2team1.mentoview.user.domain.entity.User;
import ce2team1.mentoview.user.domain.entity.atrribute.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndProviderId(String email, String providerId);

    List<User> findAllByStatus(UserStatus status);
}