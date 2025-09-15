package edtech.afrilingo.certification;

import edtech.afrilingo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    
    Optional<Certificate> findByCertificateId(String certificateId);
    
    List<Certificate> findByUserOrderByIssuedAtDesc(User user);
    
    List<Certificate> findByLanguageTestedAndVerified(String language, boolean verified);
    
    boolean existsByUserAndLanguageTestedAndVerified(User user, String language, boolean verified);

    // New: Fetch all certificates ordered by issued date (newest first)
    List<Certificate> findAllByOrderByIssuedAtDesc();
}