package edtech.afrilingo.userProgress;

import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.profile.UserDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edtech.afrilingo.user.User;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "User Dashboard", description = "Endpoints for user dashboard data")
public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    @Operation(summary = "Get user dashboard data", description = "Returns comprehensive data for the user dashboard")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserDashboard() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Map<String, Object> dashboardData = userDashboardService.getUserDashboardData(currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success(dashboardData));
    }
}