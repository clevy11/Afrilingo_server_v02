package edtech.afrilingo.certification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationQuestionResponseRepository extends JpaRepository<CertificationQuestionResponse, Long> {
    
    List<CertificationQuestionResponse> findBySessionOrderByAnsweredAtAsc(CertificationSession session);
    
    @Query("SELECT r FROM CertificationQuestionResponse r JOIN FETCH r.question WHERE r.session = :session ORDER BY r.answeredAt ASC")
    List<CertificationQuestionResponse> findBySessionWithQuestionOrderByAnsweredAtAsc(@Param("session") CertificationSession session);
    
    @Query("SELECT AVG(r.timeSpentMs) FROM CertificationQuestionResponse r WHERE r.session.id = :sessionId")
    Double getAverageTimeSpentBySession(Long sessionId);
    
    @Query("SELECT COUNT(r) FROM CertificationQuestionResponse r WHERE r.session.id = :sessionId AND r.correct = true")
    Long getCorrectAnswersCount(Long sessionId);
}
