package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationSetMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void findAllReturnsPagedAnnotationSets() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AnnotationSet> annotationSetPage = Page.empty();
        when(annotationSetRepository.findAll(pageable)).thenReturn(annotationSetPage);
        when(annotationSetMapper.toDto(any(AnnotationSet.class))).thenReturn(new AnnotationSetDTO());

        Page<AnnotationSetDTO> result = annotationSetServiceImpl.findAll(pageable);

        Assertions.assertNotNull(result);
        verify(annotationSetRepository).findAll(pageable);
    }

    @Test
    @Transactional
    void findOneByIdReturnsAnnotationSetIfExists() {
        UUID id = UUID.randomUUID();
        AnnotationSet annotationSet = new AnnotationSet();
        when(annotationSetRepository.findById(id)).thenReturn(Optional.of(annotationSet));
        when(annotationSetMapper.toDto(annotationSet)).thenReturn(new AnnotationSetDTO());

        Optional<AnnotationSetDTO> result = annotationSetServiceImpl.findOne(id);

        assertTrue(result.isPresent());
        verify(annotationSetRepository).findById(id);
    }

    @Test
    @Transactional
    void findOneByIdReturnsEmptyIfNotExists() {
        UUID id = UUID.randomUUID();
        when(annotationSetRepository.findById(id)).thenReturn(Optional.empty());

        Optional<AnnotationSetDTO> result = annotationSetServiceImpl.findOne(id);

        assertFalse(result.isPresent());
        verify(annotationSetRepository).findById(id);
    }

    @Test
    @Transactional
    void deleteAnnotationSetByIdDeletesSuccessfully() {
        UUID id = UUID.randomUUID();

        annotationSetServiceImpl.delete(id);

        verify(annotationSetRepository).deleteById(id);
    }

    @Test
    @Transactional
    void findOneByDocumentIdReturnsAnnotationSetForValidUser() {
        String documentId = "doc123";
        AnnotationSet annotationSet = new AnnotationSet();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testUser"));
        when(annotationSetRepository.findByDocumentIdAndCreatedBy(documentId, "testUser"))
                .thenReturn(Optional.of(annotationSet));
        when(annotationSetMapper.toDto(annotationSet)).thenReturn(new AnnotationSetDTO());

        Optional<AnnotationSetDTO> result = annotationSetServiceImpl.findOneByDocumentId(documentId);

        assertTrue(result.isPresent());
        verify(annotationSetRepository).findByDocumentIdAndCreatedBy(documentId, "testUser");
    }

}
