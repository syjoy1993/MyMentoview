package ce2team1.mentoview.repository;

import ce2team1.mentoview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
}