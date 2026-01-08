package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.CcdService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnotationResourceTest {

    @Mock
    private AnnotationService annotationService;

    @Mock
    private CcdService ccdService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AnnotationResource annotationResource;

    private AnnotationDTO annotationDTO;
    private UUID annotationId;

    private static final String ENTITY_CREATION_ALERT = "X-emannotationapp-alert";
    private static final String ENTITY_UPDATE_ALERT = "X-emannotationapp-alert";
    private static final String ENTITY_DELETION_ALERT = "X-emannotationapp-alert";

    @BeforeEach
    void setUp() {
        annotationId = UUID.randomUUID();
        annotationDTO = new AnnotationDTO();
        annotationDTO.setId(annotationId);
        annotationDTO.setAnnotationSetId(UUID.randomUUID());
        annotationDTO.setDocumentId("documentId");
    }

    @Test
    void createAnnotationSuccess() throws URISyntaxException, PSQLException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(ccdService.buildCommentHeader(any(AnnotationDTO.class), anyString())).thenReturn("header");
        when(annotationService.save(any(AnnotationDTO.class))).thenReturn(annotationDTO);
        when(annotationService.findOne(annotationId, true)).thenReturn(Optional.of(annotationDTO));

        ResponseEntity<AnnotationDTO> response = annotationResource.createAnnotation(request, annotationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(annotationDTO);
        assertThat(response.getHeaders().getFirst(ENTITY_CREATION_ALERT)).isNotNull();
        assertThat(response.getHeaders().getLocation()).hasPath("/api/annotations/" + annotationId);

        verify(ccdService).buildCommentHeader(annotationDTO, "Bearer token");
        verify(annotationService).save(annotationDTO);
    }

    @Test
    void createAnnotationThrowsBadRequestAlertExceptionWhenIdIsNull() {
        annotationDTO.setId(null);

        assertThatThrownBy(() -> annotationResource.createAnnotation(request, annotationDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid id");
    }

    @Test
    void createAnnotationReturnsBadRequestOnDataIntegrityViolationException() throws URISyntaxException, PSQLException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(annotationService.save(any(AnnotationDTO.class)))
            .thenThrow(new DataIntegrityViolationException("Constraint violation"));

        ResponseEntity<AnnotationDTO> response = annotationResource.createAnnotation(request, annotationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createAnnotationReturnsBadRequestOnPsqlException() throws URISyntaxException, PSQLException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        PSQLException psqlException = new PSQLException("DB Error", PSQLState.UNKNOWN_STATE);

        when(annotationService.save(any(AnnotationDTO.class))).thenThrow(psqlException);

        ResponseEntity<AnnotationDTO> response = annotationResource.createAnnotation(request, annotationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createAnnotationReturnsBadRequestOnConstraintViolationException() throws URISyntaxException, PSQLException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(annotationService.save(any(AnnotationDTO.class)))
            .thenThrow(new ConstraintViolationException("Validation error", null));

        ResponseEntity<AnnotationDTO> response = annotationResource.createAnnotation(request, annotationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createAnnotationReturnsBadRequestWhenFindOneReturnsEmpty() throws URISyntaxException, PSQLException {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(annotationService.save(any(AnnotationDTO.class))).thenReturn(annotationDTO);
        when(annotationService.findOne(annotationId, true)).thenReturn(Optional.empty());

        ResponseEntity<AnnotationDTO> response = annotationResource.createAnnotation(request, annotationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateAnnotationSuccess() throws PSQLException {
        when(annotationService.save(any(AnnotationDTO.class))).thenReturn(annotationDTO);

        ResponseEntity<AnnotationDTO> response = annotationResource.updateAnnotation(annotationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(annotationDTO);
        assertThat(response.getHeaders().getFirst(ENTITY_UPDATE_ALERT)).isNotNull();
    }

    @Test
    void updateAnnotationThrowsBadRequestAlertExceptionWhenIdIsNull() {
        annotationDTO.setId(null);

        assertThatThrownBy(() -> annotationResource.updateAnnotation(annotationDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid id");
    }

    @Test
    void updateAnnotationReturnsBadRequestWhenDataIntegrityViolationExceptionThrown() throws PSQLException {
        when(annotationService.save(any(AnnotationDTO.class)))
            .thenThrow(new DataIntegrityViolationException("Error"));

        ResponseEntity<AnnotationDTO> response = annotationResource.updateAnnotation(annotationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateAnnotationReturnsBadRequestWhenPsqlExceptionThrown() throws PSQLException {
        PSQLException psqlException = new PSQLException("DB Error", PSQLState.UNKNOWN_STATE);
        when(annotationService.save(any(AnnotationDTO.class))).thenThrow(psqlException);

        ResponseEntity<AnnotationDTO> response = annotationResource.updateAnnotation(annotationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateAnnotationReturnsBadRequestWhenConstraintViolationExceptionThrown() throws PSQLException {
        when(annotationService.save(any(AnnotationDTO.class)))
            .thenThrow(new ConstraintViolationException("Validation error", null));

        ResponseEntity<AnnotationDTO> response = annotationResource.updateAnnotation(annotationDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllAnnotationsSuccess() {
        Pageable pageable = Pageable.unpaged();
        Page<AnnotationDTO> page = new PageImpl<>(List.of(annotationDTO));
        when(annotationService.findAll(pageable)).thenReturn(page);

        ResponseEntity<List<AnnotationDTO>> response = annotationResource.getAllAnnotations(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(annotationDTO);
        assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");
    }

    @Test
    void getAnnotationSuccess() {
        when(annotationService.findOne(annotationId)).thenReturn(Optional.of(annotationDTO));

        ResponseEntity<AnnotationDTO> response = annotationResource.getAnnotation(annotationId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(annotationDTO);
    }

    @Test
    void getAnnotationReturnsNotFound() {
        when(annotationService.findOne(annotationId)).thenReturn(Optional.empty());

        ResponseEntity<AnnotationDTO> response = annotationResource.getAnnotation(annotationId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteAnnotationSuccess() {
        ResponseEntity<Void> response = annotationResource.deleteAnnotation(annotationId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(ENTITY_DELETION_ALERT)).isNotNull();

        verify(annotationService).delete(annotationId);
    }
}