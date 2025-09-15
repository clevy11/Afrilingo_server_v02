package edtech.afrilingo.profile;

import edtech.afrilingo.exception.ResourceNotFoundException;
import edtech.afrilingo.language.Language;
import edtech.afrilingo.language.LanguageService;
import edtech.afrilingo.user.User;
import edtech.afrilingo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final LanguageService languageService;

    @Override
    public Optional<UserProfile> getUserProfileById(Long id) {
        return userProfileRepository.findById(id);
    }

    @Override
    public Optional<UserProfile> getUserProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public UserProfile createOrUpdateUserProfile(Long userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Update User entity
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        userRepository.save(user);

        // Find or create UserProfile
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());

        profile.setUser(user);
        profile.setCountry(request.getCountry());
        profile.setFirstLanguage(request.getFirstLanguage());
        profile.setReasonToLearn(request.getReasonToLearn());
        profile.setProfilePicture(request.getProfilePicture());

        if (request.getDailyReminders() != null) {
            profile.setDailyReminders(request.getDailyReminders());
        }
        if (request.getDailyGoalMinutes() != null) {
            profile.setDailyGoalMinutes(request.getDailyGoalMinutes());
        }
        if (request.getPreferredLearningTime() != null) {
            profile.setPreferredLearningTime(request.getPreferredLearningTime());
        }

        if (request.getLanguagesToLearnIds() != null && !request.getLanguagesToLearnIds().isEmpty()) {
            List<Language> languages = languageService.getLanguagesByIds(request.getLanguagesToLearnIds());
            if (languages.size() != request.getLanguagesToLearnIds().size()) {
                throw new ResourceNotFoundException("One or more languages could not be found.");
            }
            profile.setLanguagesToLearn(languages);
        }

        return userProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public UserProfile createOrUpdateUserProfile(Long userId, UserProfile userProfile) {
        // Find or create user profile
        UserProfile existingProfile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Update fields
        existingProfile.setUser(user);
        existingProfile.setCountry(userProfile.getCountry());
        existingProfile.setFirstLanguage(userProfile.getFirstLanguage());
        existingProfile.setReasonToLearn(userProfile.getReasonToLearn());
        
        // Set profile picture if provided
        if (userProfile.getProfilePicture() != null) {
            existingProfile.setProfilePicture(userProfile.getProfilePicture());
        }
        
        // Set learning preferences if provided
        if (userProfile.isDailyReminders() || userProfile.getDailyGoalMinutes() > 0) {
            existingProfile.setDailyReminders(userProfile.isDailyReminders());
            existingProfile.setDailyGoalMinutes(userProfile.getDailyGoalMinutes());
        }
        
        if (userProfile.getPreferredLearningTime() != null) {
            existingProfile.setPreferredLearningTime(userProfile.getPreferredLearningTime());
        }
        
        // Process languages to learn if provided
        if (userProfile.getLanguagesToLearn() != null && !userProfile.getLanguagesToLearn().isEmpty()) {
            List<Language> languages = new ArrayList<>();
            for (Language lang : userProfile.getLanguagesToLearn()) {
                if (lang.getId() != null) {
                    languageService.getLanguageById(lang.getId())
                            .ifPresent(languages::add);
                } else if (lang.getCode() != null) {
                    languageService.getLanguageByCode(lang.getCode())
                            .ifPresent(languages::add);
                }
            }
            existingProfile.setLanguagesToLearn(languages);
        }

        return userProfileRepository.save(existingProfile);
    }

    @Override
    @Transactional
    public UserProfile updateLanguagesToLearn(Long userId, List<Long> languageIds) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "userId", userId));
        
        List<Language> languages = languageIds.stream()
                .map(id -> languageService.getLanguageById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Language", "id", id)))
                .collect(Collectors.toList());
        
        userProfile.setLanguagesToLearn(languages);
        
        return userProfileRepository.save(userProfile);
    }

    @Override
    @Transactional
    public UserProfile updateLearningPreferences(Long userId, boolean dailyReminders, 
                                                int dailyGoalMinutes, String preferredLearningTime) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "userId", userId));
        
        userProfile.setDailyReminders(dailyReminders);
        userProfile.setDailyGoalMinutes(dailyGoalMinutes);
        
        if (preferredLearningTime != null) {
            userProfile.setPreferredLearningTime(preferredLearningTime);
        }
        
        return userProfileRepository.save(userProfile);
    }

    @Override
    @Transactional
    public UserProfile updateProfilePicture(Long userId, String profilePicture) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "userId", userId));
        
        userProfile.setProfilePicture(profilePicture);
        
        return userProfileRepository.save(userProfile);
    }

    @Override
    @Transactional
    public boolean deleteUserProfile(Long id) {
        return userProfileRepository.findById(id)
                .map(profile -> {
                    userProfileRepository.delete(profile);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<UserProfile> getUserProfilesByCountry(String country) {
        return userProfileRepository.findByCountry(country);
    }

    @Override
    public List<UserProfile> getUserProfilesByFirstLanguage(String firstLanguage) {
        return userProfileRepository.findByFirstLanguage(firstLanguage);
    }

    @Override
    public List<UserProfile> getUserProfilesByLanguageToLearn(Long languageId) {
        return userProfileRepository.findByLanguageToLearn(languageId);
    }

    @Override
    public boolean hasUserProfile(Long userId) {
        return userProfileRepository.existsByUserId(userId);
    }
}