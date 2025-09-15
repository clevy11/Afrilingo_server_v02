package edtech.afrilingo.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {
    private String firstName;
    private String lastName;
    private String country;
    private String firstLanguage;
    private String reasonToLearn;
    private String profilePicture;
    private List<Long> languagesToLearnIds;
    private Boolean dailyReminders;
    private Integer dailyGoalMinutes;
    private String preferredLearningTime;
}