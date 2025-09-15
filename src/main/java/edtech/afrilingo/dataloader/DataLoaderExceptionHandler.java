package edtech.afrilingo.dataloader;

import edtech.afrilingo.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for data loader-specific exceptions.
 */
@ControllerAdvice
public class DataLoaderExceptionHandler {

    /**
     * Handles DataLoaderException and returns an appropriate ApiResponse.
     *
     * @param ex the DataLoaderException
     * @return ResponseEntity containing ApiResponse with error details
     */
    @ExceptionHandler(DataLoaderException.class)
    public ResponseEntity<ApiResponse<String>> handleDataLoaderException(DataLoaderException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        // Build error message with code
        String errorMessage = String.format("Data loading error [%s]: %s", 
                ex.getErrorCode().name(), ex.getMessage());
        
        ApiResponse<String> response = ApiResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(errorMessage)
                .build();
        
        return new ResponseEntity<>(response, status);
    }
}