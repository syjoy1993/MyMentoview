package ce2team1.mentoview.repository;

import ce2team1.mentoview.entity.InterviewResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InterviewResponseRepository extends JpaRepository<InterviewResponse, Long> {

    @Query("SELECT r FROM InterviewResponse r " +
            "JOIN FETCH r.question q " +
            "JOIN FETCH q.interview i " +
            "WHERE r.responseId = :responseId")
    InterviewResponse findByIdWithQuestionAndInterview(Long responseId);
}