package edtech.afrilingo.notification;

import edtech.afrilingo.user.User;
import edtech.afrilingo.user.UserRepository;
import edtech.afrilingo.userProgress.UserProgressRepository;
import edtech.afrilingo.userProgress.UserQuizAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyInactivityReminderJob {

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final UserQuizAttemptRepository userQuizAttemptRepository;
    private final NotificationEventPublisher notificationPublisher;

    // Run every day at 20:00 server time
    @Scheduled(cron = "0 0 20 * * *")
    public void sendInactivityReminders() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<User> allUsers = userRepository.findAll();
        int reminded = 0;

        for (User user : allUsers) {
            boolean hasLesson = userProgressRepository
                    .findByUserId(user.getId()).stream()
                    .anyMatch(p -> p.getCompletedAt() != null &&
                            !p.getCompletedAt().isBefore(startOfDay) && !p.getCompletedAt().isAfter(endOfDay));

            boolean hasQuiz = userQuizAttemptRepository
                    .findByUserId(user.getId()).stream()
                    .anyMatch(a -> a.getAttemptedAt() != null &&
                            !a.getAttemptedAt().isBefore(startOfDay) && !a.getAttemptedAt().isAfter(endOfDay));

            if (!(hasLesson || hasQuiz)) {
                notificationPublisher.sendReminder(user.getId(),
                        "It's a great time to learn! Keep your streak alive today.", null);
                reminded++;
            }
        }

        log.info("Daily inactivity reminders sent to {} users", reminded);
    }
}


