package edtech.afrilingo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String USERS_CACHE = "users_all";
    public static final String COURSES_CACHE = "courses_all";
    public static final String LESSONS_CACHE = "lessons_all";
    public static final String QUESTIONS_CACHE = "questions_all";
    public static final String LESSON_CONTENTS_CACHE = "lesson_contents_all";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                USERS_CACHE,
                COURSES_CACHE,
                LESSONS_CACHE,
                QUESTIONS_CACHE,
                LESSON_CONTENTS_CACHE
        );
        manager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(5_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
        );
        return manager;
    }
}
