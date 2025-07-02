package uk.gov.hmcts.reform.em.annotation.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.Comment;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationRepository;
import uk.gov.hmcts.reform.em.annotation.service.TagService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationMapper;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AnnotationServiceImplTest {

    @Mock
    private AnnotationRepository annotationRepository;

    @Mock
    private AnnotationMapper annotationMapper;

    @Mock
    private TagService tagService;

    @Mock
    private AnnotationSetServiceImpl annotationSetService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private AnnotationServiceImpl annotationServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Fix: set annotationService field to the test instance
        try {
            Field field = AnnotationServiceImpl.class.getDeclaredField("annotationService");
            field.setAccessible(true);
            field.set(annotationServiceImpl, annotationServiceImpl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveAnnotationCreatesNewAnnotationSetWhenNotFound() throws Exception {
        AnnotationDTO annotationDTO = new AnnotationDTO();
        annotationDTO.setAnnotationSetId(UUID.randomUUID());
        annotationDTO.setDocumentId("doc123");
        annotationDTO.setTags(new HashSet<>());
        when(annotationSetService.findOne(annotationDTO.getAnnotationSetId())).thenReturn(Optional.empty());
        when(annotationSetService.save(any(AnnotationSetDTO.class))).thenReturn(new AnnotationSetDTO());
        when(annotationRepository.save(any(Annotation.class))).thenReturn(new Annotation());
        when(annotationMapper.toEntity(annotationDTO)).thenReturn(new Annotation());
        when(annotationMapper.toDto(any(Annotation.class))).thenReturn(annotationDTO);

        AnnotationDTO result = annotationServiceImpl.save(annotationDTO);

        Assertions.assertNotNull(result);
        verify(annotationSetService).save(any(AnnotationSetDTO.class));
        verify(annotationRepository).save(any(Annotation.class));
    }

    @Test
    void findOneReturnsAnnotationWhenExists() {
        UUID id = UUID.randomUUID();
        Annotation annotation = new Annotation();
        when(annotationRepository.findById(id)).thenReturn(Optional.of(annotation));
        when(annotationMapper.toDto(annotation)).thenReturn(new AnnotationDTO());

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id);

        assertTrue(result.isPresent());
        verify(annotationRepository).findById(id);
    }

    @Test
    void findOneReturnsEmptyWhenAnnotationDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(annotationRepository.findById(id)).thenReturn(Optional.empty());

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id);

        assertFalse(result.isPresent());
        verify(annotationRepository).findById(id);
    }

    @Test
    void deleteAnnotationDeletesSuccessfully() {
        UUID id = UUID.randomUUID();

        annotationServiceImpl.delete(id);

        verify(annotationRepository).deleteById(id);
    }

    @Test
    void saveAnnotationPersistsTagsWithCreatedBy() throws Exception {
        AnnotationDTO annotationDTO = new AnnotationDTO();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("test-tag");
        annotationDTO.setTags(Set.of(tagDTO));
        annotationDTO.setCreatedBy("user123");
        Annotation annotation = new Annotation();
        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        annotation.addTag(tag);
        when(annotationMapper.toEntity(annotationDTO)).thenReturn(annotation);
        when(annotationRepository.save(annotation)).thenReturn(annotation);
        when(annotationMapper.toDto(annotation)).thenReturn(annotationDTO);

        AnnotationDTO result = annotationServiceImpl.save(annotationDTO);

        Assertions.assertNotNull(result);
        verify(tagService).persistTag(any());
    }

    @Test
    void saveAnnotationPersistsRectangles() throws Exception {

        RectangleDTO rectangleDTO = new RectangleDTO();
        rectangleDTO.setId(UUID.randomUUID());
        rectangleDTO.setX(10.0);
        rectangleDTO.setY(20.0);
        rectangleDTO.setWidth(100.0);
        rectangleDTO.setHeight(200.0);
        AnnotationDTO annotationDTO = new AnnotationDTO();
        annotationDTO.setRectangles(Set.of(rectangleDTO));
        annotationDTO.setCreatedBy("user123");

        Rectangle rectangle = new Rectangle();
        rectangle.setId(rectangleDTO.getId());
        rectangle.setX(rectangleDTO.getX());
        rectangle.setY(rectangleDTO.getY());
        rectangle.setWidth(rectangleDTO.getWidth());
        rectangle.setHeight(rectangleDTO.getHeight());
        Annotation annotation = new Annotation();
        annotation.setRectangles(new HashSet<>(Set.of(rectangle)));

        when(annotationMapper.toEntity(annotationDTO)).thenReturn(annotation);
        when(annotationRepository.save(annotation)).thenReturn(annotation);
        when(annotationMapper.toDto(annotation)).thenReturn(annotationDTO);

        AnnotationDTO result = annotationServiceImpl.save(annotationDTO);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getRectangles().isEmpty());
    }

    @Test
    void saveAnnotationPersistsComments() throws Exception {
        AnnotationDTO annotationDTO = new AnnotationDTO();
        CommentDTO commentsDTO = new CommentDTO();
        commentsDTO.setId(UUID.randomUUID());
        commentsDTO.setContent("Test comment");
        annotationDTO.setComments(Set.of(commentsDTO));
        annotationDTO.setCreatedBy("user123");

        Annotation annotation = new Annotation();
        Comment comment = new Comment();
        comment.setId(commentsDTO.getId());
        comment.setContent(commentsDTO.getContent());
        annotation.setComments(new HashSet<>(Set.of(comment)));

        when(annotationMapper.toEntity(annotationDTO)).thenReturn(annotation);
        when(annotationRepository.save(annotation)).thenReturn(annotation);
        when(annotationMapper.toDto(annotation)).thenReturn(annotationDTO);

        AnnotationDTO result = annotationServiceImpl.save(annotationDTO);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getComments().isEmpty());
    }

    @Test
    void findAllReturnsPageOfAnnotationsWhenDataExists() {
        Pageable pageable = Pageable.unpaged();
        Annotation annotation = new Annotation();
        AnnotationDTO annotationDTO = new AnnotationDTO();
        when(annotationRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(annotation)));
        when(annotationMapper.toDto(annotation)).thenReturn(annotationDTO);

        Page<AnnotationDTO> result = annotationServiceImpl.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        verify(annotationRepository).findAll(pageable);
        verify(annotationMapper).toDto(annotation);
    }

    @Test
    void findOneWithoutRefreshReturnsAnnotationWhenExists() {
        UUID id = UUID.randomUUID();
        Annotation annotation = new Annotation();
        when(annotationRepository.findById(id)).thenReturn(Optional.of(annotation));
        when(annotationMapper.toDto(annotation)).thenReturn(new AnnotationDTO());

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id, false);

        assertTrue(result.isPresent());
        verify(annotationRepository).findById(id);
        verify(annotationMapper).toDto(annotation);
        verifyNoInteractions(entityManager);
    }

    @Test
    void findOneWithRefreshHandlesEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        Annotation annotation = new Annotation();
        when(annotationRepository.findById(id)).thenReturn(Optional.of(annotation));
        doThrow(new EntityNotFoundException()).when(entityManager).refresh(annotation);

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id, true);

        assertFalse(result.isPresent());
        verify(entityManager).refresh(annotation);
        verify(annotationRepository).findById(id);
        verify(annotationMapper).toDto(annotation);
    }
}
