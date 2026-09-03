package uk.gov.hmcts.reform.em.annotation.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    /*
     * Support for Hibernate types in Jackson.
     */
    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }

    /*
     * Jackson BlackbirdModule module to speed up serialization/deserialization.
     */
    @Bean
    public BlackbirdModule blackbirdModule() {
        return new BlackbirdModule();
    }

}
