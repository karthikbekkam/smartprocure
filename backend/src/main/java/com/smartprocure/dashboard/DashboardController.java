package com.smartprocure.dashboard;

import com.smartprocure.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<DashboardDto>> getMetrics() {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard metrics retrieved", dashboardService.getDashboardMetrics()));
    }
}
