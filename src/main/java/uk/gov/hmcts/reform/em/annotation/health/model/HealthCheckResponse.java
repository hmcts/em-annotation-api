package uk.gov.hmcts.reform.em.annotation.health.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HealthCheckResponse {
    private String status;
}
