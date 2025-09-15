package edtech.afrilingo.language;

import java.util.List;
import java.util.Optional;

public interface LanguageService {
    
    /**
     * Get all available languages
     * @return List of all languages
     */
    List<Language> getAllLanguages();
    
    /**
     * Get language by ID
     * @param id Language ID
     * @return Optional containing the language if found
     */
    Optional<Language> getLanguageById(Long id);

    /**
     * Get a list of languages by their IDs
     * @param ids List of language IDs
     * @return List of languages found
     */
    List<Language> getLanguagesByIds(List<Long> ids);
    
    /**
     * Get language by code (e.g., "SW" for Swahili)
     * @param code Language code
     * @return Optional containing the language if found
     */
    Optional<Language> getLanguageByCode(String code);
    
    /**
     * Create a new language
     * @param language Language to create
     * @return Created language with generated ID
     */
    Language createLanguage(Language language);
    
    /**
     * Update an existing language
     * @param id Language ID
     * @param languageDetails Updated language details
     * @return Updated language
     */
    Language updateLanguage(Long id, Language languageDetails);
    
    /**
     * Delete a language
     * @param id Language ID
     * @return true if deleted successfully
     */
    boolean deleteLanguage(Long id);
    
    /**
     * Check if language exists by ID
     * @param id Language ID
     * @return true if language exists
     */
    boolean existsById(Long id);
    
    /**
     * Check if language exists by code
     * @param code Language code
     * @return true if language exists
     */
    boolean existsByCode(String code);
}