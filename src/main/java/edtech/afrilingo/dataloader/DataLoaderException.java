package edtech.afrilingo.dataloader;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there's an issue with data loading operations.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataLoaderException extends RuntimeException {

    private final DataLoaderErrorCode errorCode;

    /**
     * Constructs a new DataLoaderException with the specified message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code representing the type of error
     *
    public DataLoaderException(String message, DataLoaderErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new DataLoaderException with the specified message, cause, and error code.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param errorCode the error code representing the type of error
     */
    public DataLoaderException(String message, Throwable cause, DataLoaderErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code for this exception.
     *
     * @return the error code
     */
    public DataLoaderErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Error codes for data loading operations.
     */
    public enum DataLoaderErrorCode {
        /**
         * Error occurred while loading languages.
         */
        LANGUAGE_LOAD_ERROR,
        
        /**
         * Error occurred while loading courses.
         */
        COURSE_LOAD_ERROR,
        
        /**
         * Error occurred while loading lessons.
         */
        LESSON_LOAD_ERROR,
        
        /**
         * Error occurred while loading lesson content.
         */
        LESSON_CONTENT_LOAD_ERROR,
        
        /**
         * Error occurred while loading quizzes.
         */
        QUIZ_LOAD_ERROR,
        
        /**
         * Error occurred while loading questions.
         */
        QUESTION_LOAD_ERROR,
        
        /**
         * Error occurred while loading options.
         */
        OPTION_LOAD_ERROR,
        
        /**
         * Error occurred while loading users.
         */
        USER_LOAD_ERROR,
        
        /**
         * Error occurred while loading user profiles.
         */
        USER_PROFILE_LOAD_ERROR,
        
        /**
         * Error occurred while resetting data.
         */
        DATA_RESET_ERROR,
        
        /**
         * Generic error.
         */
        GENERAL_ERROR
    }
}