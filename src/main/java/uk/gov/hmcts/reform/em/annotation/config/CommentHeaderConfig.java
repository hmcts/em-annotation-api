package uk.gov.hmcts.reform.em.annotation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;

@Configuration
@ConfigurationProperties(prefix = "configuration.comment-header")
public class CommentHeaderConfig {
    private final HashMap<String, ArrayList<String>> jurisdictionPaths = new HashMap<>();

    public HashMap<String, ArrayList<String>> getJurisdictionPaths() {
        return jurisdictionPaths;
    }
}
