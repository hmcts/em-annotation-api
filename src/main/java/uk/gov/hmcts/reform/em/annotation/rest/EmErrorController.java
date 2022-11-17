package uk.gov.hmcts.reform.em.annotation.rest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Error controller class to rethrow errors occurring outside of controller methods from inside a controller.
 * This enables existing exception handlers to act regarding the error.
 */
@Controller
public class ErrorControllerImpl implements ErrorController {
    @GetMapping("/error")
    public void handleError(HttpServletRequest request) throws Throwable {
        if (request.getAttribute(javax.servlet.RequestDispatcher.ERROR_EXCEPTION) != null) {
            throw (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        }
    }
}
