package uk.gov.hmcts.reform.em.annotation.provider;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerConsumerVersionSelectors;
import au.com.dius.pact.provider.junitsupport.loader.SelectorBuilder;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;

import java.time.Instant;
import java.util.UUID;

@Import(ContractTestProviderConfiguration.class)
@IgnoreNoPactsToVerify
@AutoConfigureMockMvc(addFilters = false)
//Uncomment @PactFolder and comment the @PactBroker line to test local consumer.
//using this, import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
//@PactFolder("target/pacts")
@PactBroker(
    url = "${PACT_BROKER_FULL_URL:http://localhost:80}",
    providerBranch = "${pact.provider.branch}"
)
public abstract class BaseProviderTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static final UUID EXAMPLE_USER_ID = UUID.fromString("c38fd29e-fa2e-43d4-a599-2d3f2908565b");
    protected static final Instant EXAMPLE_DATE = Instant.parse("2024-01-15T10:00:00.123Z");

    @BeforeEach
    void setupPactVerification(PactVerificationContext context) {
        MockMvcTestTarget testTarget = new MockMvcTestTarget(mockMvc);
        testTarget.setControllers(getControllersUnderTest());

        if (context != null) {
            context.setTarget(testTarget);
        }

        testTarget.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper));
    }

    protected abstract Object[] getControllersUnderTest();

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        if (context != null) {
            context.verifyInteraction();
        }
    }


    @PactBrokerConsumerVersionSelectors
    public static SelectorBuilder consumerVersionSelectors() {
        return new SelectorBuilder()
            .branch("master")
            .matchingBranch()
            .deployedOrReleased();

    }

    protected IdamDetails createIdamDetails() {
        IdamDetails details = new IdamDetails();
        details.setForename("Test");
        details.setSurname("User");
        details.setEmail("test.user.annotations@example.com");
        return details;
    }
}