package ce2team1.mentoview.repository;

import ce2team1.mentoview.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {

    @Query("SELECT iq FROM InterviewQuestion iq " +
            "JOIN FETCH iq.interviewResponse ir " +
            "JOIN FETCH iq.interviewFeedback ifb " +
            "WHERE iq.interview.interviewId = :interviewId")
    List<InterviewQuestion> findQuestionsWithResponsesAndFeedback(Long interviewId);

    List<InterviewQuestion> findAllByQuestionIdIn(Collection<Long> questionIds);
}