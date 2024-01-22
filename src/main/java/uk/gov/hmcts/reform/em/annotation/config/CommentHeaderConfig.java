package uk.gov.hmcts.reform.em.annotation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "configuration.comment-header")
public class CommentHeaderConfig {
    private final Map<String, List<String>> jurisdictionPaths = new HashMap<>();

    public Map<String, List<String>> getJurisdictionPaths() {
        return jurisdictionPaths;
    }
}
