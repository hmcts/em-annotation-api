package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Error controller class to rethrow errors occurring outside of controller methods from inside a controller.
 * This enables existing exception handlers to act regarding the error.
 */
@Controller
public class EmErrorController implements ErrorController {

    private final Logger log = LoggerFactory.getLogger(EmErrorController.class);

    @SuppressWarnings("java:S112")
    @GetMapping("/error")
    public void handleError(HttpServletRequest request) throws Throwable {
        if (request.getAttribute(jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION) != null) {
            Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            log.error("EM Annotation Error Controller : {}", throwable.getMessage());
            throw throwable;
        }
    }
}
