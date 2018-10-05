package uk.gov.hmcts.reform.em.annotation.config.security;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.HttpComponentsBasedUserTokenParser;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenParser;
import uk.gov.hmcts.reform.em.annotation.authchecker.EmUserTokenDetails;

@Configuration
public class EmSecurityConfiguration {

    @Bean
    public UserTokenParser<EmUserTokenDetails> emUserTokenParser(HttpClient userTokenParserHttpClient,
                                                                 @Value("${auth.idam.client.baseUrl}") String baseUrl) {
        return new HttpComponentsBasedUserTokenParser<>(userTokenParserHttpClient, baseUrl, EmUserTokenDetails.class);
    }

}
