package com.smartprocure.web.rest;

import com.smartprocure.dto.response.ApiResponse;
import com.smartprocure.dto.response.DashboardMetricsDTO;
import com.smartprocure.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard Analytics", description = "Endpoints for retrieving high-level enterprise metrics")
public class DashboardRestController {

    private final DashboardService dashboardService;

    @GetMapping("/metrics")
    @Operation(summary = "Get Dashboard Metrics", description = "Retrieve aggregated KPI statistics for executive overview")
    public ResponseEntity<ApiResponse> getMetrics() {
        DashboardMetricsDTO metrics = dashboardService.getMetrics();
        return ResponseEntity.ok(ApiResponse.ok("Dashboard metrics retrieved", metrics));
    }
}
