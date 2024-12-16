package uk.gov.hmcts.reform.em.annotation.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * This is a Unit Test to test the interaction of IdamRepository with its dependencies (ie
 * idamClient ) . Please see src/aat/java/functional package for tests that cover Integration
 * scenarios which involves passing in valid auth tokens to verify the complete flow. ie (
 * Microservice --> OAuthFilter --> Endpoint )
 */
@ExtendWith(SpringExtension.class)
class IdamRepositoryTest {

    @Mock
    private IdamClient idamClient;

    private IdamRepository idamRepository;

    @BeforeEach
    void setup() {
        this.idamRepository = new IdamRepository(idamClient);
    }

    @Test
    @DisplayName("UserInfo should be called by IdamClient ")
    void testGetUserInfo() {

        
        final UserInfo userInfo = UserInfo.builder()
                .uid("100")
                .givenName("John")
                .familyName("Doe")
                .roles(asList("Admin", "CaseWorker"))
                .build();
        when(idamClient.getUserInfo(any(String.class))).thenReturn(userInfo);

        idamRepository.getUserInfo(random(5, true, false));

        verify(idamClient, times(1)).getUserInfo(anyString());
    }
}
