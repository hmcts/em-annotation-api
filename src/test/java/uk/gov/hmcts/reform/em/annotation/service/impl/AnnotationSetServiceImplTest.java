package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ResourceNotFoundException;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationSetMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnnotationSetServiceImplTest {

    @Mock
    private AnnotationSetRepository annotationSetRepository;

    @Mock
    private AnnotationSetMapper annotationSetMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private AnnotationSetServiceImpl annotationSetServiceImpl;

    private static final String CURRENT_USER = "testUser";
    private static final String OTHER_USER = "otherUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
    }

    @Test
    @Transactional
    void saveAnnotationSetPersistsEntitySuccessfully() {
        AnnotationSetDTO annotationSetDTO = new AnnotationSetDTO();
        annotationSetDTO.setId(UUID.randomUUID());
        AnnotationSet annotationSet = new AnnotationSet();
        when(annotationSetMapper.toEntity(annotationSetDTO)).thenReturn(annotationSet);
        when(annotationSetRepository.save(annotationSet)).thenReturn(annotationSet);
        when(annotationSetMapper.toDto(annotationSet)).thenReturn(annotationSetDTO);

        AnnotationSetDTO result = annotationSetServiceImpl.save(annotationSetDTO);

        Assertions.assertNotNull(result);
        verify(annotationSetRepository).save(annotationSet);
    }

    @Test
    @Transactional
    void findAllReturnsPagedAnnotationSetsForCurrentUser() {
        Pageable pageable = PageRequest.of(0, 10);
        AnnotationSet annotationSet = new AnnotationSet();
        when(annotationSetRepository.findByCreatedBy(eq(CURRENT_USER), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(annotationSet)));
        when(annotationSetMapper.toDto(annotationSet)).thenReturn(new AnnotationSetDTO());

        Page<AnnotationSetDTO> result = annotationSetServiceImpl.findAll(pageable);

        Assertions.assertNotNull(result);
        verify(annotationSetRepository).findByCreatedBy(CURRENT_USER, pageable);
    }

    @Test
    @Transactional
    void findOneByIdReturnsAnnotationSetWhenOwner() {
        UUID id = UUID.randomUUID();
        AnnotationSet annotationSet = new AnnotationSet();
        when(annotationSetRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.of(annotationSet));
        when(annotationSetMapper.toDto(annotationSet)).thenReturn(new AnnotationSetDTO());

        Optional<AnnotationSetDTO> result = annotationSetServiceImpl.findOne(id);

        assertTrue(result.isPresent());
        verify(annotationSetRepository).findByIdAndCreatedBy(id, CURRENT_USER);
    }

    @Test
    @Transactional
    void findOneByIdReturnsEmptyWhenNotOwner() {
        UUID id = UUID.randomUUID();
        when(annotationSetRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.empty());

        Optional<AnnotationSetDTO> result = annotationSetServiceImpl.findOne(id);

        assertFalse(result.isPresent());
        verify(annotationSetRepository).findByIdAndCreatedBy(id, CURRENT_USER);
    }

    @Test
    @Transactional
    void findOneByIdReturnsEmptyIfNotExists() {
        UUID id = UUID.randomUUID();
        when(annotationSetRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.empty());

        Optional<AnnotationSetDTO> result = annotationSetServiceImpl.findOne(id);

        assertFalse(result.isPresent());
        verify(annotationSetRepository).findByIdAndCreatedBy(id, CURRENT_USER);
    }

    @Test
    @Transactional
    void deleteAnnotationSetByIdDeletesSuccessfully() {
        UUID id = UUID.randomUUID();
        AnnotationSet annotationSet = new AnnotationSet();
        annotationSet.setCreatedBy(CURRENT_USER);
        when(annotationSetRepository.findById(id)).thenReturn(Optional.of(annotationSet));

        annotationSetServiceImpl.delete(id);

        verify(annotationSetRepository).deleteById(id);
    }

    @Test
    @Transactional
    void deleteAnnotationSetThrowsWhenNotOwner() {
        UUID id = UUID.randomUUID();
        AnnotationSet annotationSet = new AnnotationSet();
        annotationSet.setCreatedBy(OTHER_USER);
        when(annotationSetRepository.findById(id)).thenReturn(Optional.of(annotationSet));

        assertThrows(ResourceNotFoundException.class, () -> annotationSetServiceImpl.delete(id));
        verify(annotationSetRepository, never()).deleteById(any());
    }

    @Test
    @Transactional
    void findOneByDocumentIdReturnsAnnotationSetForValidUser() {
        String documentId = "doc123";
        AnnotationSet annotationSet = new AnnotationSet();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(annotationSetRepository.findByDocumentIdAndCreatedBy(documentId, CURRENT_USER))
                .thenReturn(Optional.of(annotationSet));
        when(annotationSetMapper.toDto(annotationSet)).thenReturn(new AnnotationSetDTO());

        Optional<AnnotationSetDTO> result = annotationSetServiceImpl.findOneByDocumentId(documentId);

        assertTrue(result.isPresent());
        verify(annotationSetRepository).findByDocumentIdAndCreatedBy(documentId, CURRENT_USER);
    }

    @Test
    void getCurrentUserThrowsWhenNotAuthenticated() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> annotationSetServiceImpl.findAll(PageRequest.of(0, 10)));
    }
}
