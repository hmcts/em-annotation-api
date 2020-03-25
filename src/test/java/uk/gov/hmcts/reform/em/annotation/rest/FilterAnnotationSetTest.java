package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ExceptionTranslator;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationSetMapper;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Test class for the FilterAnnotationSet REST controller.
 *
 * @see AnnotationSetResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class FilterAnnotationSetTest extends BaseTest {

    @Autowired
    private AnnotationSetRepository annotationSetRepository;

    @Autowired
    private AnnotationSetMapper annotationSetMapper;

    @Autowired
    private AnnotationSetService annotationSetService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @MockBean
    private SecurityUtils securityUtils;

    private AnnotationSet annotationSet;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FilterAnnotationSet filterAnnotationSet = new FilterAnnotationSet(annotationSetService);
        em.persist(new IdamDetails("system"));
    }

    public static AnnotationSet createEntity(EntityManager em) {
        AnnotationSet annotationSet = new AnnotationSet();

        annotationSet.setId(UUID.randomUUID());
        annotationSet.setDocumentId("documentId");
        annotationSet.setAnnotations(new HashSet<>());
        annotationSet.setCreatedBy("system");

        return annotationSet;
    }

    @Before
    public void initTest() {
        annotationSet = createEntity(em);
    }

    @Test
    @Transactional
    public void getAnnotationSetWithDocumentId() throws Exception {
        annotationSetRepository.saveAndFlush(annotationSet);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("system"));

        restLogoutMockMvc.perform(get("/api/annotation-sets/filter")
                .param("documentId", "documentId"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(annotationSet.getId().toString()))
                .andExpect(jsonPath("$.documentId").value(annotationSet.getDocumentId()));
    }

    @Test
    @Transactional
    public void createNewAnnotationSetWhenOptionalEmpty() throws Exception {
        restLogoutMockMvc.perform(get("/api/annotation-sets/filter")
                .param("documentId", "documentId"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.documentId").value(annotationSet.getDocumentId()));
    }
}
