package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.test.retry.RetryExtension;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestUtil.class})
@TestPropertySource(value = "classpath:application.yml")
@ExtendWith({SerenityJUnit5Extension.class, SpringExtension.class})
@WithTags({@WithTag("testType:Functional")})
public abstract class BaseTest {

    protected final TestUtil testUtil;

    @Value("${test.url}")
    protected String testUrl;

    @RegisterExtension
    protected RetryExtension retryExtension = new RetryExtension(3);

    protected RequestSpecification request;
    protected RequestSpecification unAuthenticatedRequest;

    @Autowired
    protected BaseTest(TestUtil testUtil) {
        this.testUtil = testUtil;
    }

    @BeforeEach
    public void setupRequestSpecification() {
        request = testUtil
            .authRequest()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE);

        unAuthenticatedRequest = testUtil
            .unauthenticatedRequest()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE);
    }
}