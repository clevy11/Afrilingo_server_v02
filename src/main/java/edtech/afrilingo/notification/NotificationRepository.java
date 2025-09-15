package edtech.afrilingo.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    Page<Notification> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

    List<Notification> findByUserIdAndReadOrderByTimestampDesc(Long userId, boolean read);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = ?1 AND n.read = false")
    long countUnreadNotifications(Long userId);

    // New methods for enhanced functionality
    List<Notification> findByIdInAndUserId(List<Long> ids, Long userId);

    Page<Notification> findByUserIdAndTypeOrderByTimestampDesc(Long userId, NotificationType type, Pageable pageable);

    long countByUserId(Long userId);

    long countByUserIdAndType(Long userId, NotificationType type);

    long countByUserIdAndTimestampAfter(Long userId, LocalDateTime timestamp);

    void deleteByUserId(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.snoozed = true AND n.snoozedUntil <= ?1")
    List<Notification> findExpiredSnoozedNotifications(LocalDateTime currentTime);

    @Query("SELECT n FROM Notification n WHERE n.user.id = ?1 AND n.read = false AND (n.snoozed = false OR n.snoozedUntil <= ?2)")
    List<Notification> findActiveUnreadNotifications(Long userId, LocalDateTime currentTime);

    @Query("SELECT n FROM Notification n WHERE n.timestamp < ?1")
    List<Notification> findOldNotifications(LocalDateTime cutoffDate);
}