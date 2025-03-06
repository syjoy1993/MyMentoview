package ce2team1.mentoview.repository;

import ce2team1.mentoview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findAllByResumeResumeId(Long resumeId);
}