package edtech.afrilingo.certification;

import edtech.afrilingo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificationSessionRepository extends JpaRepository<CertificationSession, Long> {
    
    Optional<CertificationSession> findBySessionId(String sessionId);
    
    Optional<CertificationSession> findByUserAndCompletedFalse(User user);
    
    List<CertificationSession> findByUserOrderByStartTimeDesc(User user);
    
    List<CertificationSession> findByLanguageCodeAndCompleted(String languageCode, boolean completed);
    
    @Query("SELECT s FROM CertificationSession s WHERE s.startTime >= :fromDate AND s.completed = true")
    List<CertificationSession> findCompletedSessionsAfter(LocalDateTime fromDate);
    
    @Query("SELECT s FROM CertificationSession s WHERE s.suspiciousActivityCount > :threshold")
    List<CertificationSession> findSessionsWithSuspiciousActivity(int threshold);
    
    List<CertificationSession> findByCompletedFalse();
}
