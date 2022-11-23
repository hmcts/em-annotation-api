package uk.gov.hmcts.reform.em.annotation.data.migration;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

import java.util.stream.Stream;

public class FlywayNoOpStrategy implements FlywayMigrationStrategy {
    Logger log = LoggerFactory.getLogger(FlywayNoOpStrategy.class);

    @Override
    public void migrate(Flyway flyway) {
        log.info("FlywayNoOpStrategy invoked to migrate");

        Stream.of(flyway.info().all())
                .peek(migrationInfo -> log.info("Info of script {} : state {}", migrationInfo.getScript(), migrationInfo.getState()))
                .filter(info -> !"V1__baseline_migration.sql".equals(info.getScript()))
                .filter(info -> !info.getState().isApplied())
                .findFirst()
                .ifPresent(info -> {
                    throw new PendingMigrationScriptException(info.getScript());
                });
    }
}
