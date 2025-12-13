package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
class DocumentDataRollbackIntTest extends BaseTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @MockitoBean
    private AnnotationSetRepository annotationSetRepository;

    @MockitoBean(name = "authTokenValidator")
    private AuthTokenValidator authTokenValidator;

    private UUID documentId;
    private Bookmark bookmark;
    private static final String MOCK_TOKEN = "Bearer mock_token";

    @BeforeEach
    void setup() {
        documentId = UUID.randomUUID();

        new TransactionTemplate(transactionManager).execute(status -> {
            if (Objects.isNull(em.find(IdamDetails.class, "system"))) {
                em.persist(new IdamDetails("system"));
            }
            return null;
        });

        bookmark = new Bookmark();
        bookmark.setId(UUID.randomUUID());
        bookmark.setDocumentId(documentId);
        bookmark.setCreatedBy("system");
        bookmark.setName("Rollback Test");
        bookmark.setPageNumber(1);
        bookmarkRepository.saveAndFlush(bookmark);
    }

    @Test
    void testTransactionRollback() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn("em_gw");

        doThrow(new RuntimeException("Simulated DB Failure"))
            .when(annotationSetRepository).findAllByDocumentId(anyString());

        restLogoutMockMvc.perform(delete("/api/documents/{docId}/data", documentId)
                .header("ServiceAuthorization", MOCK_TOKEN))
            .andExpect(status().isInternalServerError());

        assertThat(bookmarkRepository.findById(bookmark.getId())).isPresent();
    }
}