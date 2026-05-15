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
import org.springframework.security.authentication.BadCredentialsException;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.Comment;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ResourceNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
    private SecurityUtils securityUtils;

    @Mock
    private EntityManager entityManager;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private AnnotationServiceImpl annotationServiceImpl;

    private static final String CURRENT_USER = "testUser";
    private static final String OTHER_USER = "otherUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            Field field = AnnotationServiceImpl.class.getDeclaredField("annotationService");
            field.setAccessible(true);
            field.set(annotationServiceImpl, annotationServiceImpl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
    }

    @Test
    void saveAnnotationCreatesNewAnnotationSetWhenNotFound() throws Exception {
        AnnotationDTO annotationDTO = new AnnotationDTO();
        annotationDTO.setAnnotationSetId(UUID.randomUUID());
        annotationDTO.setDocumentId("doc123");
        annotationDTO.setTags(new HashSet<>());
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("user1"));
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
    void findOneReturnsAnnotationWhenOwner() {
        UUID id = UUID.randomUUID();
        Annotation annotation = new Annotation();
        when(annotationRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.of(annotation));
        when(annotationMapper.toDto(annotation)).thenReturn(new AnnotationDTO());

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id);

        assertTrue(result.isPresent());
        verify(annotationRepository).findByIdAndCreatedBy(id, CURRENT_USER);
    }

    @Test
    void findOneReturnsEmptyWhenNotOwner() {
        UUID id = UUID.randomUUID();
        when(annotationRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.empty());

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id);

        assertFalse(result.isPresent());
        verify(annotationRepository).findByIdAndCreatedBy(id, CURRENT_USER);
    }

    @Test
    void findOneReturnsEmptyWhenAnnotationDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(annotationRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.empty());

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id);

        assertFalse(result.isPresent());
        verify(annotationRepository).findByIdAndCreatedBy(id, CURRENT_USER);
    }

    @Test
    void deleteAnnotationDeletesSuccessfully() {
        UUID id = UUID.randomUUID();
        Annotation annotation = new Annotation();
        annotation.setCreatedBy(CURRENT_USER);
        when(annotationRepository.findById(id)).thenReturn(Optional.of(annotation));

        annotationServiceImpl.delete(id);

        verify(annotationRepository).deleteById(id);
    }

    @Test
    void deleteAnnotationThrowsWhenNotOwner() {
        UUID id = UUID.randomUUID();
        Annotation annotation = new Annotation();
        annotation.setCreatedBy(OTHER_USER);
        when(annotationRepository.findById(id)).thenReturn(Optional.of(annotation));

        assertThrows(ResourceNotFoundException.class, () -> annotationServiceImpl.delete(id));
        verify(annotationRepository, never()).deleteById(any());

    }
  
    @Test
    void saveAnnotationThrowsWhenNoAuthenticatedUser() {
        AnnotationDTO annotationDTO = new AnnotationDTO();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("test-tag");
        annotationDTO.setTags(Set.of(tagDTO));
        Annotation annotation = new Annotation();
        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        annotation.addTag(tag);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());
        when(annotationMapper.toEntity(annotationDTO)).thenReturn(annotation);

        Assertions.assertThrows(IllegalStateException.class, () -> annotationServiceImpl.save(annotationDTO));
    }

    @Test
    void saveAnnotationPersistsTagsWithCreatedBy() throws Exception {
        AnnotationDTO annotationDTO = new AnnotationDTO();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("test-tag");
        tagDTO.setCreatedBy("forgedUser");
        annotationDTO.setTags(Set.of(tagDTO));
        annotationDTO.setCreatedBy("forgedUser");
        Annotation annotation = new Annotation();
        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        annotation.addTag(tag);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("user1"));
        when(annotationMapper.toEntity(annotationDTO)).thenReturn(annotation);
        when(annotationRepository.save(annotation)).thenReturn(annotation);
        when(annotationMapper.toDto(annotation)).thenReturn(annotationDTO);

        annotationServiceImpl.save(annotationDTO);

        Assertions.assertEquals("user1", tagDTO.getCreatedBy());
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

        Rectangle rectangle = new Rectangle();
        rectangle.setId(rectangleDTO.getId());
        rectangle.setX(rectangleDTO.getX());
        rectangle.setY(rectangleDTO.getY());
        rectangle.setWidth(rectangleDTO.getWidth());
        rectangle.setHeight(rectangleDTO.getHeight());
        Annotation annotation = new Annotation();
        annotation.setRectangles(new HashSet<>(Set.of(rectangle)));

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("user1"));
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

        Annotation annotation = new Annotation();
        Comment comment = new Comment();
        comment.setId(commentsDTO.getId());
        comment.setContent(commentsDTO.getContent());
        annotation.setComments(new HashSet<>(Set.of(comment)));

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("user1"));
        when(annotationMapper.toEntity(annotationDTO)).thenReturn(annotation);
        when(annotationRepository.save(annotation)).thenReturn(annotation);
        when(annotationMapper.toDto(annotation)).thenReturn(annotationDTO);

        AnnotationDTO result = annotationServiceImpl.save(annotationDTO);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getComments().isEmpty());
    }

    @Test
    void findAllReturnsPageOfAnnotationsForCurrentUser() {
        Pageable pageable = Pageable.unpaged();
        Annotation annotation = new Annotation();
        AnnotationDTO annotationDTO = new AnnotationDTO();
        when(annotationRepository.findByCreatedBy(eq(CURRENT_USER), eq(pageable)))
            .thenReturn(new PageImpl<>(List.of(annotation)));
        when(annotationMapper.toDto(annotation)).thenReturn(annotationDTO);

        Page<AnnotationDTO> result = annotationServiceImpl.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        verify(annotationRepository).findByCreatedBy(CURRENT_USER, pageable);
        verify(annotationMapper).toDto(annotation);
    }

    @Test
    void findOneWithoutRefreshReturnsAnnotationWhenOwner() {
        UUID id = UUID.randomUUID();
        Annotation annotation = new Annotation();
        when(annotationRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.of(annotation));
        when(annotationMapper.toDto(annotation)).thenReturn(new AnnotationDTO());

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id, false);

        assertTrue(result.isPresent());
        verify(annotationRepository).findByIdAndCreatedBy(id, CURRENT_USER);
        verify(annotationMapper).toDto(annotation);
        verifyNoInteractions(entityManager);
    }

    @Test
    void findOneWithoutRefreshReturnsEmptyWhenNotOwner() {
        UUID id = UUID.randomUUID();
        when(annotationRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.empty());

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id, false);

        assertFalse(result.isPresent());
        verify(annotationRepository).findByIdAndCreatedBy(id, CURRENT_USER);
        verifyNoInteractions(entityManager);
    }

    @Test
    void findOneWithRefreshHandlesEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        Annotation annotation = new Annotation();
        when(annotationRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.of(annotation));
        doThrow(new EntityNotFoundException()).when(entityManager).refresh(annotation);

        Optional<AnnotationDTO> result = annotationServiceImpl.findOne(id, true);

        assertFalse(result.isPresent());
        verify(entityManager).refresh(annotation);
        verify(annotationRepository).findByIdAndCreatedBy(id, CURRENT_USER);
        verify(annotationMapper).toDto(annotation);
    }

    @Test
    void getCurrentUserThrowsWhenNotAuthenticated() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> annotationServiceImpl.findAll(Pageable.unpaged()));
    }
}
