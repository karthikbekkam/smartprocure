package com.smartprocure.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Response Envelope containing generated JWT bearer token and logged-in user summary.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private UserSummaryResponse user;
}
