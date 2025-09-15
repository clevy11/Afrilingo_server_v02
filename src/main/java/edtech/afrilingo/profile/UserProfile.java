package edtech.afrilingo.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edtech.afrilingo.language.Language;
import edtech.afrilingo.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;
    private String firstLanguage;


    @Column(name = "profile_picture", columnDefinition = "text")
    private String profilePicture;
    
    @Column(name = "learning_reason")
    private String reasonToLearn;

    // A user can have preferences for multiple languages to learn
    @ManyToMany
    @JoinTable(
        name = "user_languages_to_learn",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    @JsonIgnore
    private List<Language> languagesToLearn;
    
    // Learning preferences
    private boolean dailyReminders;
    private int dailyGoalMinutes;
    private String preferredLearningTime;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}