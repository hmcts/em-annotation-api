package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
class EmErrorControllerIntTest extends BaseTest {

    @Test
    void testPath() throws Exception {
        restLogoutMockMvc.perform(get("/error")
                )
                .andExpect(status().isOk());
    }

    @Test
    void testException() throws Exception {
        restLogoutMockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_EXCEPTION,
                                new IllegalStateException("Test Exception"))
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testHandledException() throws Exception {
        restLogoutMockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_EXCEPTION,
                                new ConcurrencyFailureException("Test concurrency failure"))
                )
                .andExpect(status().isConflict());
    }

}
