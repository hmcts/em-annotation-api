package uk.gov.hmcts.reform.em.annotation.rest.errors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import org.zalando.problem.Status;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for the Exception errors
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ErrorExceptionsTest extends BaseTest {

    @Autowired
    private ExceptionTranslatorTestController controller;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;


    @Test
    public void emailNotFoundExceptionTest() {
        EmailNotFoundException emailNotFoundException = new EmailNotFoundException();
        assertThat(emailNotFoundException.getStatus()).isEqualTo(Status.BAD_REQUEST);
        assertThat(emailNotFoundException.getTitle()).isEqualTo("Email address not registered");
        assertThat(emailNotFoundException.getType()).isEqualTo(ErrorConstants.EMAIL_NOT_FOUND_TYPE);
    }

    @Test
    public void emailAlreadyUsedExceptionTest() {
        EmailAlreadyUsedException emailAlreadyUsedException = new EmailAlreadyUsedException();
        assertThat(emailAlreadyUsedException.getType()).isEqualTo(ErrorConstants.EMAIL_ALREADY_USED_TYPE);
        assertThat(emailAlreadyUsedException.getLocalizedMessage()).isEqualTo("Email is already in use!");
        assertThat(emailAlreadyUsedException.getEntityName()).isEqualTo("userManagement");
        assertThat(emailAlreadyUsedException.getErrorKey()).isEqualTo("emailexists");
    }

    @Test
    public void loginAlreadyUsedExceptionTest() {
        LoginAlreadyUsedException loginAlreadyUsedException = new LoginAlreadyUsedException();
        assertThat(loginAlreadyUsedException.getType()).isEqualTo(ErrorConstants.LOGIN_ALREADY_USED_TYPE);
        assertThat(loginAlreadyUsedException.getLocalizedMessage()).isEqualTo("Login name already used!");
        assertThat(loginAlreadyUsedException.getEntityName()).isEqualTo("userManagement");
        assertThat(loginAlreadyUsedException.getErrorKey()).isEqualTo("userexists");
    }

    @Test
    public void internalServerErrorExceptionTest() {
        InternalServerErrorException internalServerErrorException = new InternalServerErrorException("Internal Server Error");
        assertThat(internalServerErrorException.getType()).isEqualTo(ErrorConstants.DEFAULT_TYPE);
        assertThat(internalServerErrorException.getLocalizedMessage()).isEqualTo("Internal Server Error");
        assertThat(internalServerErrorException.getStatus()).isEqualTo(Status.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void invalidPasswordExceptionTest() {
        InvalidPasswordException invalidPasswordException = new InvalidPasswordException();
        assertThat(invalidPasswordException.getType()).isEqualTo(ErrorConstants.INVALID_PASSWORD_TYPE);
        assertThat(invalidPasswordException.getLocalizedMessage()).isEqualTo("Incorrect password");
        assertThat(invalidPasswordException.getStatus()).isEqualTo(Status.BAD_REQUEST);
    }


}
