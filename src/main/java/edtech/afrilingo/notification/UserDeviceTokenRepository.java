// UserDeviceTokenRepository.java
package edtech.afrilingo.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    
    Optional<UserDeviceToken> findByToken(String token);
    
    @Query("SELECT udt FROM UserDeviceToken udt WHERE udt.token = ?1 AND udt.user.id = ?2")
    Optional<UserDeviceToken> findByTokenAndUserId(String token, Long userId);
    
    List<UserDeviceToken> findByUserIdAndActiveTrue(Long userId);
    
    @Query("SELECT udt FROM UserDeviceToken udt WHERE udt.active = true AND udt.lastUsedAt < ?1")
    List<UserDeviceToken> findInactiveTokens(LocalDateTime cutoffDate);
    
    void deleteByUserIdAndToken(Long userId, String token);
}