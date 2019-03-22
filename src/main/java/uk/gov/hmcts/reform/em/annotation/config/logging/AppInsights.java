package uk.gov.hmcts.reform.em.annotation.config.logging;

import com.microsoft.applicationinsights.TelemetryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("azure.app_insights_key")
public class AppInsights {

    private static final Logger LOG = LoggerFactory.getLogger(AppInsights.class);

    @Autowired
    public AppInsights(TelemetryClient client) {
        LOG.info("Building AppInsights");
    }
}