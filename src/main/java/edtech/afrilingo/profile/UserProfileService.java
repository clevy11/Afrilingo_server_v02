package edtech.afrilingo.profile;

import java.util.List;
import java.util.Optional;

public interface UserProfileService {
    /**
     * Get user profile by ID
     * @param id User profile ID
     * @return Optional containing the user profile if found
     */
    Optional<UserProfile> getUserProfileById(Long id);
    
    /**
     * Get user profile by user ID
     * @param userId User ID
     * @return Optional containing the user profile if found
     */
    Optional<UserProfile> getUserProfileByUserId(Long userId);
    
    /**
     * Create or update user profile
     * @param userId User ID
     * @param userProfile User profile data
     * @return Created or updated user profile
     */
    UserProfile createOrUpdateUserProfile(Long userId, UserProfileRequest userProfileRequest);
    
    /**
     * Create or update user profile
     * @param userId User ID
     * @param userProfile User profile data
     * @return Created or updated user profile
     */
    UserProfile createOrUpdateUserProfile(Long userId, UserProfile userProfile);
    
    /**
     * Update user profile languages to learn
     * @param userId User ID
     * @param languageIds List of language IDs
     * @return Updated user profile
     */
    UserProfile updateLanguagesToLearn(Long userId, List<Long> languageIds);
    
    /**
     * Update user learning preferences
     * @param userId User ID
     * @param dailyReminders Daily reminders enabled
     * @param dailyGoalMinutes Daily goal in minutes
     * @param preferredLearningTime Preferred learning time
     * @return Updated user profile
     */
    UserProfile updateLearningPreferences(Long userId, boolean dailyReminders, 
                                            int dailyGoalMinutes, String preferredLearningTime);
    
    /**
     * Update user profile picture
     * @param userId User ID
     * @param profilePicture Profile picture URL
     * @return Updated user profile
     */
    UserProfile updateProfilePicture(Long userId, String profilePicture);
    
    /**
     * Delete user profile
     * @param id User profile ID
     * @return true if deleted successfully
     */
    boolean deleteUserProfile(Long id);
    
    /**
     * Get user profiles by country
     * @param country Country
     * @return List of user profiles
     */
    List<UserProfile> getUserProfilesByCountry(String country);
    
    /**
     * Get user profiles by first language
     * @param firstLanguage First language
     * @return List of user profiles
     */
    List<UserProfile> getUserProfilesByFirstLanguage(String firstLanguage);
    
    /**
     * Get user profiles by language to learn
     * @param languageId Language ID
     * @return List of user profiles
     */
    List<UserProfile> getUserProfilesByLanguageToLearn(Long languageId);
    
    /**
     * Check if user has a profile
     * @param userId User ID
     * @return true if user has profile
     */
    boolean hasUserProfile(Long userId);
}