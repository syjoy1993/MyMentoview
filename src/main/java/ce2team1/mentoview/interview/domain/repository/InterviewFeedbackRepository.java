package ce2team1.mentoview.interview.domain.repository;

import ce2team1.mentoview.interview.domain.entity.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedback, Long> {
    @Query( "SELECT COUNT(if) FROM InterviewFeedback if WHERE if.question.interview.interviewId = :interviewId")
    long countByInterviewId(Long interviewId);
}