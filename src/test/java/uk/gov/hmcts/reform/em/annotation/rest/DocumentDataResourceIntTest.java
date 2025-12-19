package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.domain.Comment;
import uk.gov.hmcts.reform.em.annotation.domain.EntityAuditEvent;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.domain.Metadata;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationRepository;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;
import uk.gov.hmcts.reform.em.annotation.repository.CommentRepository;
import uk.gov.hmcts.reform.em.annotation.repository.EntityAuditEventRepository;
import uk.gov.hmcts.reform.em.annotation.repository.MetadataRepository;
import uk.gov.hmcts.reform.em.annotation.repository.RectangleRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
class DocumentDataResourceIntTest extends BaseTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private AnnotationSetRepository annotationSetRepository;
    @Autowired
    private AnnotationRepository annotationRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private RectangleRepository rectangleRepository;
    @Autowired
    private MetadataRepository metadataRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private EntityAuditEventRepository entityAuditEventRepository;

    @MockitoBean(name = "authTokenValidator")
    private AuthTokenValidator authTokenValidator;

    private UUID documentId;
    private static final String SERVICE_AUTH_HEADER = "ServiceAuthorization";
    private static final String MOCK_TOKEN = "Bearer mock_token";

    @BeforeEach
    void setup() {
        documentId = UUID.randomUUID();
        if (Objects.isNull(em.find(IdamDetails.class, "system"))) {
            em.persist(new IdamDetails("system"));
            em.flush();
        }
    }

    @Test
    @Transactional
    @DisplayName("Should delete all data when service is authorized")
    void deleteDocumentData() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn("em_gw");

        Bookmark bookmark = new Bookmark();
        bookmark.setId(UUID.randomUUID());
        bookmark.setDocumentId(documentId);
        bookmark.setCreatedBy("system");
        bookmark.setName("Test Bookmark");
        bookmark.setPageNumber(1);
        bookmarkRepository.saveAndFlush(bookmark);

        AnnotationSet annotationSet = new AnnotationSet();
        annotationSet.setId(UUID.randomUUID());
        annotationSet.setDocumentId(documentId.toString());
        annotationSet.setCreatedBy("system");
        annotationSetRepository.saveAndFlush(annotationSet);

        Metadata metadata = new Metadata();
        metadata.setDocumentId(documentId);
        metadata.setRotationAngle(90);
        metadata.setCreatedBy("system");
        metadataRepository.saveAndFlush(metadata);

        restLogoutMockMvc.perform(delete("/api/documents/{docId}/data", documentId)
                .header(SERVICE_AUTH_HEADER, MOCK_TOKEN))
            .andExpect(status().isNoContent());

        assertThat(bookmarkRepository.findAll()).isEmpty();
        assertThat(annotationSetRepository.findAll()).isEmpty();
        assertThat(metadataRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should delete data AND audit logs")
    void deleteDocumentDataAndAudits() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn("em_gw");

        entityAuditEventRepository.deleteAll();
        em.flush();

        AnnotationSet annotationSet = new AnnotationSet();
        UUID annotationSetId = UUID.randomUUID();
        annotationSet.setId(annotationSetId);
        annotationSet.setDocumentId(documentId.toString());
        annotationSet.setCreatedBy("system");
        annotationSetRepository.saveAndFlush(annotationSet);

        em.flush();
        em.clear();

        // Wait for async audit event to be written
        await().atMost(Duration.ofSeconds(2))
            .pollInterval(Duration.ofMillis(100))
            .untilAsserted(() -> {
                List<EntityAuditEvent> audits = entityAuditEventRepository.findAll();
                assertThat(audits).isNotEmpty();
                assertThat(audits.stream()
                    .anyMatch(audit -> audit.getEntityId().equals(annotationSetId)))
                    .isTrue();
            });

        List<EntityAuditEvent> auditsBeforeDelete = entityAuditEventRepository.findAll();
        assertThat(auditsBeforeDelete).isNotEmpty();

        restLogoutMockMvc.perform(delete("/api/documents/{docId}/data", documentId)
                .header(SERVICE_AUTH_HEADER, MOCK_TOKEN))
            .andExpect(status().isNoContent());

        em.flush();
        em.clear();

        assertThat(annotationSetRepository.findAll()).isEmpty();
        assertThat(entityAuditEventRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should delete full hierarchy (AnnotationSet -> Annotation -> Rectangle, Comment) and all audits")
    void deleteFullHierarchy() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn("em_gw");

        entityAuditEventRepository.deleteAll();
        em.flush();

        AnnotationSet annotationSet = new AnnotationSet();
        annotationSet.setId(UUID.randomUUID());
        annotationSet.setDocumentId(documentId.toString());
        annotationSet.setCreatedBy("system");
        annotationSetRepository.saveAndFlush(annotationSet);

        Annotation annotation = new Annotation();
        annotation.setId(UUID.randomUUID());
        annotation.setAnnotationSet(annotationSet);
        annotation.setCreatedBy("system");
        annotation.setPage(1);
        annotation.setColor("red");
        annotation.setAnnotationType("highlight");
        annotationRepository.saveAndFlush(annotation);

        Rectangle rectangle = new Rectangle();
        rectangle.setId(UUID.randomUUID());
        rectangle.setAnnotation(annotation);
        rectangle.setCreatedBy("system");
        rectangle.setX(10.0);
        rectangle.setY(10.0);
        rectangle.setWidth(100.0);
        rectangle.setHeight(50.0);
        rectangleRepository.saveAndFlush(rectangle);

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setAnnotation(annotation);
        comment.setCreatedBy("system");
        comment.setContent("Test comment");
        commentRepository.saveAndFlush(comment);

        Bookmark bookmark = new Bookmark();
        bookmark.setId(UUID.randomUUID());
        bookmark.setDocumentId(documentId);
        bookmark.setCreatedBy("system");
        bookmark.setName("Test Bookmark");
        bookmark.setPageNumber(1);
        bookmarkRepository.saveAndFlush(bookmark);

        Metadata metadata = new Metadata();
        metadata.setDocumentId(documentId);
        metadata.setRotationAngle(90);
        metadata.setCreatedBy("system");
        metadataRepository.saveAndFlush(metadata);

        em.flush();
        em.clear();

        // Wait for CREATE audit events to be written
        await().atMost(Duration.ofSeconds(2))
            .pollInterval(Duration.ofMillis(100))
            .untilAsserted(() -> {
                List<EntityAuditEvent> audits = entityAuditEventRepository.findAll();
                assertThat(audits).isNotEmpty();
            });

        assertThat(annotationSetRepository.findAll()).hasSize(1);
        assertThat(annotationRepository.findAll()).hasSize(1);
        assertThat(rectangleRepository.findAll()).hasSize(1);
        assertThat(commentRepository.findAll()).hasSize(1);
        assertThat(bookmarkRepository.findAll()).hasSize(1);
        assertThat(metadataRepository.findAll()).hasSize(1);

        List<EntityAuditEvent> auditsBeforeDelete = entityAuditEventRepository.findAll();
        assertThat(auditsBeforeDelete).isNotEmpty();

        restLogoutMockMvc.perform(delete("/api/documents/{docId}/data", documentId)
                .header(SERVICE_AUTH_HEADER, MOCK_TOKEN))
            .andExpect(status().isNoContent());

        em.flush();
        em.clear();

        assertThat(annotationSetRepository.findAll()).isEmpty();
        assertThat(annotationRepository.findAll()).isEmpty();
        assertThat(rectangleRepository.findAll()).isEmpty();
        assertThat(commentRepository.findAll()).isEmpty();
        assertThat(bookmarkRepository.findAll()).isEmpty();
        assertThat(metadataRepository.findAll()).isEmpty();
        assertThat(entityAuditEventRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should return 204 even if no data exists")
    void deleteDocumentDataIdempotency() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn("em_gw");

        UUID nonExistentDocumentId = UUID.randomUUID();

        restLogoutMockMvc.perform(delete("/api/documents/{docId}/data", nonExistentDocumentId)
                .header(SERVICE_AUTH_HEADER, MOCK_TOKEN))
            .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    @DisplayName("Should return 403 when service is NOT whitelisted")
    void deleteDocumentDataUnauthorizedService() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn("bad_service");

        restLogoutMockMvc.perform(delete("/api/documents/{docId}/data", documentId)
                .header(SERVICE_AUTH_HEADER, MOCK_TOKEN))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @DisplayName("Should return 403 when ServiceAuthorization header is missing")
    void deleteDocumentDataMissingHeader() throws Exception {
        restLogoutMockMvc.perform(delete("/api/documents/{docId}/data", documentId))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @DisplayName("Should return 400 Bad Request when document ID is not a valid UUID")
    void deleteDocumentDataInvalidUUID() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn("em_gw");

        restLogoutMockMvc.perform(delete("/api/documents/i_am_not_a_uuid/data")
                .header(SERVICE_AUTH_HEADER, MOCK_TOKEN))
            .andExpect(status().isBadRequest());
    }
}