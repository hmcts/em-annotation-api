package uk.gov.hmcts.reform.em.annotation;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.annotation.config.security.JwtGrantedAuthoritiesConverter;
import uk.gov.hmcts.reform.em.annotation.repository.IdamRepository;
import uk.gov.hmcts.reform.em.annotation.service.CcdService;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.idam.client.IdamClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

public abstract class BaseTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @MockBean
    private IdamApi idamApi;

    @MockBean
    private IdamClient idamClient;

    @MockBean
    private IdamRepository idamRepository;

    protected MockMvc restLogoutMockMvc;

    @Mock
    protected Authentication authentication;

    @Mock
    protected SecurityContext securityContext;

    @MockBean
    private AuthTokenGenerator authTokenGenerator;

    @MockBean
    protected CcdService ccdService;

    @Before
    public void setupMocks() {

        MockitoAnnotations.initMocks(this);

        doReturn(authentication).when(securityContext).getAuthentication();
        SecurityContextHolder.setContext(securityContext);
        restLogoutMockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Mockito.when(authTokenGenerator.generate()).thenReturn("Bearer x");
        Mockito.when(ccdService.getCaseDetails(anyString(), anyString(), anyString()))
                .thenReturn(Mockito.mock(CaseDetails.class));
    }
}
