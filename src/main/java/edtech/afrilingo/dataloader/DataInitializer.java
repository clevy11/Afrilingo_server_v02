package edtech.afrilingo.dataloader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DataInitializer is responsible for loading all necessary data when the application starts.
 * It implements CommandLineRunner to execute the data loading process during application startup.
 * The behavior can be controlled via configuration properties in application.properties.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DataLoaderService dataLoaderService;
    private final DataHealthService dataHealthService;

    @Value("${afrilingo.data.auto-load:true}")
    private boolean autoLoadData;

    @Value("${afrilingo.data.reset-on-startup:false}")
    private boolean resetDataOnStartup;

    @Value("${afrilingo.data.auto-repair:true}")
    private boolean autoRepairData;

    @Override
    public void run(String... args) {
        if (!autoLoadData) {
            log.info("Automatic data loading is disabled. Set afrilingo.data.auto-load=true to enable.");
            return;
        }

        log.info("Starting data initialization process...");
        try {
            if (resetDataOnStartup) {
                log.info("Resetting all existing data as per configuration...");
                dataLoaderService.resetAllData();

                // After reset, we always need to load data
                log.info("Loading application data after reset...");
                dataLoaderService.loadAllData();
            } else {
                // Check data health
                log.info("Checking data integrity...");
                Map<String, Object> healthStatus = dataHealthService.checkDataIntegrity();
                boolean isHealthy = (boolean) healthStatus.get("overallHealth");

                if (!isHealthy) {
                    log.warn("Data integrity issues detected!");

                    if (autoRepairData) {
                        log.info("Auto-repairing data...");
                        Map<String, Object> repairResults = dataHealthService.repairData();
                        log.info("Data repair completed: {}", repairResults.get("status"));
                    } else {
                        log.warn("Auto-repair is disabled. Set afrilingo.data.auto-repair=true to enable automatic data repair.");
                        // Load all data as a fallback
                        log.info("Loading all data as fallback...");
                        dataLoaderService.loadAllData();
                    }
                } else {
                    log.info("Data integrity check passed. No issues detected.");
                }
            }

            log.info("Data initialization completed successfully");
        } catch (Exception e) {
            log.error("Error during data initialization: {}", e.getMessage(), e);
            // We don't rethrow the exception to allow the application to start even if data loading fails
        }
    }
}
