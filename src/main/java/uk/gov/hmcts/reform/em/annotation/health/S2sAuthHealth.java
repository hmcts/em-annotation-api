package uk.gov.hmcts.reform.em.annotation.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class S2sAuthHealth implements HealthIndicator {

    private final WebChecker s2sAuthWebChecker;

    @Autowired
    public S2sAuthHealth(@Value("${auth.provider.service.client.baseUrl}") String s2sAuthUrl) {
        s2sAuthWebChecker = new WebChecker("s2sAuth", s2sAuthUrl, new RestTemplate());
    }

    @Override
    public Health health() {
        return s2sAuthWebChecker.health();
    }
}
