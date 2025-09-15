package edtech.afrilingo.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    /**
     * Find user profile by user ID
     * @param userId User ID
     * @return Optional containing the user profile if found
     */
    Optional<UserProfile> findByUserId(Long userId);
    
    /**
     * Find user profiles by country
     * @param country Country
     * @return List of user profiles
     */
    List<UserProfile> findByCountry(String country);
    
    /**
     * Find user profiles by first language
     * @param firstLanguage First language
     * @return List of user profiles
     */
    List<UserProfile> findByFirstLanguage(String firstLanguage);
    
    /**
     * Find user profiles by language to learn
     * @param languageId Language ID
     * @return List of user profiles
     */
    @Query("SELECT up FROM UserProfile up JOIN up.languagesToLearn l WHERE l.id = :languageId")
    List<UserProfile> findByLanguageToLearn(@Param("languageId") Long languageId);
    
    /**
     * Check if user profile exists for the user
     * @param userId User ID
     * @return true if profile exists
     */
    boolean existsByUserId(Long userId);
}