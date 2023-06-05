package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;

import javax.servlet.RequestDispatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
public class EmErrorControllerIntTest extends BaseTest {

    @Test
    public void testPath() throws Exception {
        restLogoutMockMvc.perform(get("/error")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testException() throws Exception {
        restLogoutMockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_EXCEPTION, new IllegalStateException("Test Exception"))
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testHandledException() throws Exception {
        restLogoutMockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_EXCEPTION, new ConcurrencyFailureException("Test concurrency failure"))
                )
                .andExpect(status().isConflict());
    }

}
