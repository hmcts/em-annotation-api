package uk.gov.hmcts.reform.em.annotation.data.migration;

import com.sun.istack.logging.Logger;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

import java.util.stream.Stream;

public class FlywayNoOpStrategy implements FlywayMigrationStrategy {
    Logger log = Logger.getLogger(FlywayNoOpStrategy.class);
    @Override
    public void migrate(Flyway flyway) {
        log.info("Database before migration done {}", flyway.info().all());
        Flyway.configure()
                .baselineOnMigrate(true)
                .load();
        log.info("Database migration done {}", flyway.info().all());
        Stream.of(flyway.info().all())
                .filter(info -> !info.getState().isApplied())
                .findFirst()
                .ifPresent(info -> {
                    throw new PendingMigrationScriptException(info.getScript());
                });
    }
}
