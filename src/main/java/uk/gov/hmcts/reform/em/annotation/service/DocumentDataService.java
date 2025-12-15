package uk.gov.hmcts.reform.em.annotation.service;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationRepository;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;
import uk.gov.hmcts.reform.em.annotation.repository.CommentRepository;
import uk.gov.hmcts.reform.em.annotation.repository.EntityAuditEventRepository;
import uk.gov.hmcts.reform.em.annotation.repository.MetadataRepository;
import uk.gov.hmcts.reform.em.annotation.repository.RectangleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DocumentDataService {

    private final Logger log = LoggerFactory.getLogger(DocumentDataService.class);

    private final AnnotationSetRepository annotationSetRepository;
    private final AnnotationRepository annotationRepository;
    private final RectangleRepository rectangleRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MetadataRepository metadataRepository;
    private final EntityAuditEventRepository entityAuditEventRepository;

    public DocumentDataService(AnnotationSetRepository annotationSetRepository,
                               AnnotationRepository annotationRepository,
                               RectangleRepository rectangleRepository,
                               CommentRepository commentRepository,
                               BookmarkRepository bookmarkRepository,
                               MetadataRepository metadataRepository,
                               EntityAuditEventRepository entityAuditEventRepository) {
        this.annotationSetRepository = annotationSetRepository;
        this.annotationRepository = annotationRepository;
        this.rectangleRepository = rectangleRepository;
        this.commentRepository = commentRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.metadataRepository = metadataRepository;
        this.entityAuditEventRepository = entityAuditEventRepository;
    }

    public void deleteDocumentData(UUID documentId) {
        log.info("Request to delete all data for documentId: {}", documentId);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            bookmarkRepository.deleteAllByDocumentId(documentId);
            metadataRepository.deleteAllByDocumentId(documentId);

            List<UUID> annotationSetIds = annotationSetRepository.findAllIdsByDocumentId(documentId.toString());

            if (!annotationSetIds.isEmpty()) {
                List<UUID> auditIdsToDelete = new ArrayList<>(annotationSetIds);

                List<UUID> annotationIds = annotationRepository.findAllIdsByAnnotationSetIdIn(annotationSetIds);

                if (!annotationIds.isEmpty()) {
                    auditIdsToDelete.addAll(annotationIds);

                    List<UUID> rectangleIds = rectangleRepository.findAllIdsByAnnotationIdIn(annotationIds);
                    auditIdsToDelete.addAll(rectangleIds);

                    List<UUID> commentIds = commentRepository.findAllIdsByAnnotationIdIn(annotationIds);
                    auditIdsToDelete.addAll(commentIds);

                    rectangleRepository.deleteAllByIdIn(rectangleIds);
                    commentRepository.deleteAllByIdIn(commentIds);
                    annotationRepository.deleteAllByIdIn(annotationIds);
                }

                annotationSetRepository.deleteAllByIdIn(annotationSetIds);

                if (!auditIdsToDelete.isEmpty()) {
                    entityAuditEventRepository.deleteAllByEntityIdIn(auditIdsToDelete);
                    log.debug("Deleted audit entries for {} entities.", auditIdsToDelete.size());
                }
            } else {
                log.info("No annotation sets found for documentId: {}", documentId);
            }

        } finally {
            if (stopWatch.isStarted()) {
                stopWatch.stop();
            }
            log.info("Delete execution for documentId: {} completed in {} ms",
                documentId, stopWatch.getDuration().toMillis());
        }
    }
}