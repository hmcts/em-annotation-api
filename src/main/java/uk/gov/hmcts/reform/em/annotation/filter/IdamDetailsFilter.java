package uk.gov.hmcts.reform.em.annotation.filter;

import org.springframework.web.filter.GenericFilterBean;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class IdamDetailsFilter extends GenericFilterBean {

    private final IdamDetailsFilterService idamDetailsFilterService;

    public IdamDetailsFilter(IdamDetailsFilterService idamDetailsFilterService) {
        this.idamDetailsFilterService = idamDetailsFilterService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        idamDetailsFilterService.saveIdamDetails();
        chain.doFilter(request, response);
    }

}
