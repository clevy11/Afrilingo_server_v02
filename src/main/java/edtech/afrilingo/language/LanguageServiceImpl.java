package edtech.afrilingo.language;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    @Override
    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    @Override
    public Optional<Language> getLanguageById(Long id) {
        return languageRepository.findById(id);
    }

    @Override
    public List<Language> getLanguagesByIds(List<Long> ids) {
        return languageRepository.findAllByIdIn(ids);
    }

    @Override
    public Optional<Language> getLanguageByCode(String code) {
        return languageRepository.findByCode(code);
    }

    @Override
    @Transactional
    public Language createLanguage(Language language) {
        // Validate language data
        if (language.getCode() == null || language.getName() == null) {
            throw new IllegalArgumentException("Language code and name are required");
        }
        
        // Check if language with same code already exists
        if (languageRepository.existsByCode(language.getCode())) {
            throw new IllegalArgumentException("Language with code " + language.getCode() + " already exists");
        }
        
        return languageRepository.save(language);
    }

    @Override
    @Transactional
    public Language updateLanguage(Long id, Language languageDetails) {
        return languageRepository.findById(id)
                .map(existingLanguage -> {
                    // Update fields
                    if (languageDetails.getName() != null) {
                        existingLanguage.setName(languageDetails.getName());
                    }
                    
                    if (languageDetails.getCode() != null) {
                        // Ensure new code doesn't conflict with existing language
                        if (!existingLanguage.getCode().equals(languageDetails.getCode()) && 
                            languageRepository.existsByCode(languageDetails.getCode())) {
                            throw new IllegalArgumentException("Language with code " + 
                                    languageDetails.getCode() + " already exists");
                        }
                        existingLanguage.setCode(languageDetails.getCode());
                    }
                    
                    if (languageDetails.getDescription() != null) {
                        existingLanguage.setDescription(languageDetails.getDescription());
                    }
                    
                    if (languageDetails.getFlagImage() != null) {
                        existingLanguage.setFlagImage(languageDetails.getFlagImage());
                    }
                    
                    return languageRepository.save(existingLanguage);
                })
                .orElseThrow(() -> new RuntimeException("Language not found with id " + id));
    }

    @Override
    @Transactional
    public boolean deleteLanguage(Long id) {
        return languageRepository.findById(id)
                .map(language -> {
                    languageRepository.delete(language);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean existsById(Long id) {
        return languageRepository.existsById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return languageRepository.existsByCode(code);
    }
}