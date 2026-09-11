package uk.gov.hmcts.reform.em.annotation.config;

import com.fasterxml.jackson.datatype.hibernate7.Hibernate7Module;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    /*
     * Support for Hibernate types in Jackson.
     */
    @Bean
    public Hibernate7Module hibernate7Module() {
        return new Hibernate7Module();
    }

    /*
     * Jackson BlackbirdModule module to speed up serialization/deserialization.
     */
    @Bean
    public BlackbirdModule blackbirdModule() {
        return new BlackbirdModule();
    }

}
