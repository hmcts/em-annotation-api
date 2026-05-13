package uk.gov.hmcts.reform.em.annotation;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.em.annotation.config.security.JwtGrantedAuthoritiesConverter;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.repository.IdamRepository;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.idam.client.IdamClient;

import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public abstract class BaseTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @MockitoBean
    private IdamApi idamApi;

    @MockitoBean
    private IdamClient idamClient;

    @MockitoBean
    private IdamRepository idamRepository;

    @MockitoBean
    protected SecurityUtils securityUtils;

    protected MockMvc restLogoutMockMvc;

    @Mock
    protected Authentication authentication;

    @Mock
    protected SecurityContext securityContext;

    @BeforeEach
    public void setupMocks() {

        MockitoAnnotations.openMocks(this);

        doReturn(authentication).when(securityContext).getAuthentication();
        SecurityContextHolder.setContext(securityContext);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("system"));
        restLogoutMockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
}
