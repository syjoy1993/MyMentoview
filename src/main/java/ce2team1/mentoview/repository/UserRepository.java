package ce2team1.mentoview.repository;


import ce2team1.mentoview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}