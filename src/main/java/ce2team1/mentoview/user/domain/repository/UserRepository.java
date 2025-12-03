package ce2team1.mentoview.user.domain.repository;


import ce2team1.mentoview.user.domain.entity.User;
import ce2team1.mentoview.user.domain.entity.atrribute.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndProviderId(String email, String providerId);

    List<User> findAllByStatus(UserStatus status);
}