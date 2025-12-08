package uk.gov.hmcts.reform.em.annotation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationRepository;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;
import uk.gov.hmcts.reform.em.annotation.repository.CommentRepository;
import uk.gov.hmcts.reform.em.annotation.repository.EntityAuditEventRepository;
import uk.gov.hmcts.reform.em.annotation.repository.MetadataRepository;
import uk.gov.hmcts.reform.em.annotation.repository.RectangleRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentDataServiceTest {

    @Mock
    private AnnotationSetRepository annotationSetRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private MetadataRepository metadataRepository;

    @Mock
    private AnnotationRepository annotationRepository;

    @Mock
    private RectangleRepository rectangleRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private EntityAuditEventRepository entityAuditEventRepository;

    @InjectMocks
    private DocumentDataService documentDataService;

    private final UUID documentId = UUID.randomUUID();

    @Test
    void shouldDeleteAllDataWhenSetsExist() {
        UUID annotationSetId = UUID.randomUUID();
        AnnotationSet annotationSet = new AnnotationSet();
        annotationSet.setId(annotationSetId);
        List<AnnotationSet> annotationSets = List.of(annotationSet);

        when(annotationSetRepository.findAllByDocumentId(documentId.toString()))
            .thenReturn(annotationSets);

        when(annotationRepository.findAllIdsByAnnotationSetIdIn(anyList()))
            .thenReturn(Collections.emptyList());

        documentDataService.deleteDocumentData(documentId);

        verify(bookmarkRepository).deleteAllByDocumentId(documentId);
        verify(metadataRepository).deleteAllByDocumentId(documentId);

        verify(annotationSetRepository).deleteAllByIdIn(List.of(annotationSetId));
        verify(entityAuditEventRepository).deleteAllByEntityIdIn(List.of(annotationSetId));
    }

    @Test
    void shouldDeleteFullHierarchyWithAnnotationsAndChildren() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID annotationId1 = UUID.randomUUID();
        final UUID annotationId2 = UUID.randomUUID();
        final UUID rectangleId1 = UUID.randomUUID();
        final UUID rectangleId2 = UUID.randomUUID();
        final UUID commentId = UUID.randomUUID();

        AnnotationSet annotationSet = new AnnotationSet();
        annotationSet.setId(annotationSetId);
        List<AnnotationSet> annotationSets = List.of(annotationSet);

        when(annotationSetRepository.findAllByDocumentId(documentId.toString()))
            .thenReturn(annotationSets);

        when(annotationRepository.findAllIdsByAnnotationSetIdIn(List.of(annotationSetId)))
            .thenReturn(List.of(annotationId1, annotationId2));

        when(rectangleRepository.findAllIdsByAnnotationIdIn(List.of(annotationId1, annotationId2)))
            .thenReturn(List.of(rectangleId1, rectangleId2));

        when(commentRepository.findAllIdsByAnnotationIdIn(List.of(annotationId1, annotationId2)))
            .thenReturn(List.of(commentId));

        documentDataService.deleteDocumentData(documentId);

        verify(bookmarkRepository).deleteAllByDocumentId(documentId);
        verify(metadataRepository).deleteAllByDocumentId(documentId);

        verify(rectangleRepository).deleteAllByIdIn(List.of(rectangleId1, rectangleId2));
        verify(commentRepository).deleteAllByIdIn(List.of(commentId));
        verify(annotationRepository).deleteAllByIdIn(List.of(annotationId1, annotationId2));
        verify(annotationSetRepository).deleteAllByIdIn(List.of(annotationSetId));

        List<UUID> expectedAuditIds = List.of(
            annotationSetId,
            annotationId1,
            annotationId2,
            rectangleId1,
            rectangleId2,
            commentId
        );
        verify(entityAuditEventRepository).deleteAllByEntityIdIn(expectedAuditIds);
    }

    @Test
    void shouldDeleteIndependentEntitiesButSkipSetsWhenNoneFound() {
        when(annotationSetRepository.findAllByDocumentId(documentId.toString()))
            .thenReturn(Collections.emptyList());

        documentDataService.deleteDocumentData(documentId);

        verify(bookmarkRepository).deleteAllByDocumentId(documentId);
        verify(metadataRepository).deleteAllByDocumentId(documentId);

        verify(annotationSetRepository, never()).deleteAllByIdIn(anyList());
        verify(annotationRepository, never()).deleteAllByIdIn(anyList());
        verify(rectangleRepository, never()).deleteAllByIdIn(anyList());
        verify(commentRepository, never()).deleteAllByIdIn(anyList());
        verify(entityAuditEventRepository, never()).deleteAllByEntityIdIn(anyList());
    }

    @Test
    void shouldDeleteAnnotationSetsWithoutChildrenWhenNoAnnotationsExist() {
        UUID annotationSetId = UUID.randomUUID();
        AnnotationSet annotationSet = new AnnotationSet();
        annotationSet.setId(annotationSetId);
        List<AnnotationSet> annotationSets = List.of(annotationSet);

        when(annotationSetRepository.findAllByDocumentId(documentId.toString()))
            .thenReturn(annotationSets);

        when(annotationRepository.findAllIdsByAnnotationSetIdIn(List.of(annotationSetId)))
            .thenReturn(Collections.emptyList());

        documentDataService.deleteDocumentData(documentId);

        verify(bookmarkRepository).deleteAllByDocumentId(documentId);
        verify(metadataRepository).deleteAllByDocumentId(documentId);

        verify(rectangleRepository, never()).deleteAllByIdIn(anyList());
        verify(commentRepository, never()).deleteAllByIdIn(anyList());
        verify(annotationRepository, never()).deleteAllByIdIn(anyList());

        verify(annotationSetRepository).deleteAllByIdIn(List.of(annotationSetId));
        verify(entityAuditEventRepository).deleteAllByEntityIdIn(List.of(annotationSetId));
    }
}