package uk.gov.hmcts.reform.em.annotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SuppressWarnings("HideUtilityClassConstructor")
@SpringBootApplication(scanBasePackages = {"uk.gov.hmcts.reform.em.annotation",
        "uk.gov.hmcts.reform.authorisation",
        "uk.gov.hmcts.reform.idam.client"}
)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
