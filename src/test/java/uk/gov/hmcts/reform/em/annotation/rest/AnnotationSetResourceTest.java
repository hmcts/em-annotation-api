package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnotationSetResourceTest {

    @Mock
    private AnnotationSetService annotationSetService;

    @InjectMocks
    private AnnotationSetResource annotationSetResource;

    private AnnotationSetDTO annotationSetDTO;
    private UUID annotationSetId;

    private static final String ENTITY_CREATION_ALERT = "X-emannotationapp-alert";
    private static final String ENTITY_UPDATE_ALERT = "X-emannotationapp-alert";
    private static final String ENTITY_DELETION_ALERT = "X-emannotationapp-alert";

    @BeforeEach
    void setUp() {
        annotationSetId = UUID.randomUUID();
        annotationSetDTO = new AnnotationSetDTO();
        annotationSetDTO.setId(annotationSetId);
        annotationSetDTO.setDocumentId("documentId");
    }

    @Test
    void createAnnotationSetSuccess() throws URISyntaxException {
        when(annotationSetService.save(any(AnnotationSetDTO.class))).thenReturn(annotationSetDTO);
        when(annotationSetService.findOne(annotationSetId)).thenReturn(Optional.of(annotationSetDTO));

        ResponseEntity<AnnotationSetDTO> response = annotationSetResource.createAnnotationSet(annotationSetDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(annotationSetDTO);
        assertThat(response.getHeaders().getFirst(ENTITY_CREATION_ALERT)).isNotNull();
        assertThat(response.getHeaders().getLocation()).hasPath("/api/annotation-sets/" + annotationSetId);

        verify(annotationSetService).save(annotationSetDTO);
    }

    @Test
    void createAnnotationSetThrowsBadRequestAlertExceptionWhenIdIsNull() {
        annotationSetDTO.setId(null);

        assertThatThrownBy(() -> annotationSetResource.createAnnotationSet(annotationSetDTO))
                .isInstanceOf(BadRequestAlertException.class)
                .hasMessage("Invalid id");
    }

    @Test
    void createAnnotationSetReturnsBadRequestWhenFindOneReturnsEmpty() throws URISyntaxException {
        when(annotationSetService.save(any(AnnotationSetDTO.class))).thenReturn(annotationSetDTO);
        when(annotationSetService.findOne(annotationSetId)).thenReturn(Optional.empty());

        ResponseEntity<AnnotationSetDTO> response = annotationSetResource.createAnnotationSet(annotationSetDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateAnnotationSetSuccess() throws URISyntaxException {
        when(annotationSetService.save(any(AnnotationSetDTO.class))).thenReturn(annotationSetDTO);

        ResponseEntity<AnnotationSetDTO> response = annotationSetResource.updateAnnotationSet(annotationSetDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(annotationSetDTO);
        assertThat(response.getHeaders().getFirst(ENTITY_UPDATE_ALERT)).isNotNull();
    }

    @Test
    void updateAnnotationSetThrowsBadRequestAlertExceptionWhenIdIsNull() {
        annotationSetDTO.setId(null);

        assertThatThrownBy(() -> annotationSetResource.updateAnnotationSet(annotationSetDTO))
                .isInstanceOf(BadRequestAlertException.class)
                .hasMessage("Invalid id");
    }

    @Test
    void getAllAnnotationSetsSuccess() {
        Pageable pageable = Pageable.unpaged();
        Page<AnnotationSetDTO> page = new PageImpl<>(List.of(annotationSetDTO));
        when(annotationSetService.findAll(pageable)).thenReturn(page);

        ResponseEntity<List<AnnotationSetDTO>> response = annotationSetResource.getAllAnnotationSets(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(annotationSetDTO);
        assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");
    }

    @Test
    void getAnnotationSetSuccess() {
        when(annotationSetService.findOne(annotationSetId)).thenReturn(Optional.of(annotationSetDTO));

        ResponseEntity<AnnotationSetDTO> response = annotationSetResource.getAnnotationSet(annotationSetId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(annotationSetDTO);
    }

    @Test
    void getAnnotationSetReturnsNoContent() {
        when(annotationSetService.findOne(annotationSetId)).thenReturn(Optional.empty());

        ResponseEntity<AnnotationSetDTO> response = annotationSetResource.getAnnotationSet(annotationSetId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    }

    @Test
    void deleteAnnotationSetSuccess() {
        ResponseEntity<Void> response = annotationSetResource.deleteAnnotationSet(annotationSetId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(ENTITY_DELETION_ALERT)).isNotNull();

        verify(annotationSetService).delete(annotationSetId);
    }
}