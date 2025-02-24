package ce2team1.mentoview.repository;


import ce2team1.mentoview.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}