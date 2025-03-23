package ce2team1.mentoview.admin.adminrepository;

import ce2team1.mentoview.entity.User;
import ce2team1.mentoview.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends UserRepository,AdminUserRepositoryCustom {
}
