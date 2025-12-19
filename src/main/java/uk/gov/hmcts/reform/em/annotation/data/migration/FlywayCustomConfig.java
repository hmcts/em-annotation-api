package uk.gov.hmcts.reform.em.annotation.data.migration;

import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class FlywayCustomConfig {

    @Bean
    public FlywayConfigurationCustomizer flywayCustomizer() {
        return configuration -> configuration.configuration(
            Map.of("flyway.postgresql.transactional.lock", "false")
        );
    }
}