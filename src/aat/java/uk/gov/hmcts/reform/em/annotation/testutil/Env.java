package uk.gov.hmcts.reform.em.annotation.testutil;

import org.apache.commons.lang3.Validate;

import java.util.Properties;

public class Env {

    private Env() {}

    static Properties defaults = new Properties();

    static {
        defaults.setProperty("TEST_URL", "http://localhost:8080");
    }

    public static String getTestUrl() {
        return require("TEST_URL");
    }

    public static String require(String name) {
        return Validate.notNull(System.getenv(name) == null ? defaults.getProperty(name) : System.getenv(name), "Environment variable `%s` is required", name);
    }
}
