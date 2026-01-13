package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.annotation.rest.errors.EmptyResponseException;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilterAnnotationSetTest {

    @Mock
    private AnnotationSetService annotationSetService;

    @InjectMocks
    private FilterAnnotationSet filterAnnotationSet;

    @Test
    void getAllAnnotationSetsSuccess() {
        String documentId = "doc123";
        AnnotationSetDTO annotationSetDTO = new AnnotationSetDTO();
        annotationSetDTO.setDocumentId(documentId);

        when(annotationSetService.findOneByDocumentId(documentId)).thenReturn(Optional.of(annotationSetDTO));

        ResponseEntity<AnnotationSetDTO> response = filterAnnotationSet.getAllAnnotationSets(documentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(annotationSetDTO);

        verify(annotationSetService).findOneByDocumentId(documentId);
    }

    @Test
    void getAllAnnotationSetsThrowsEmptyResponseExceptionWhenNotFound() {
        String documentId = "doc123";
        when(annotationSetService.findOneByDocumentId(documentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filterAnnotationSet.getAllAnnotationSets(documentId))
            .isInstanceOf(EmptyResponseException.class)
            .hasMessage("Could not find annotation set for this document id#doc123");
    }

    @Test
    void getAllAnnotationSetsReturnsBadRequestOnConstraintViolation() {
        String documentId = "doc123";
        when(annotationSetService.findOneByDocumentId(documentId))
            .thenThrow(new ConstraintViolationException("Violation", null));

        ResponseEntity<AnnotationSetDTO> response = filterAnnotationSet.getAllAnnotationSets(documentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllAnnotationSetsReturnsBadRequestOnDataIntegrityViolation() {
        String documentId = "doc123";
        when(annotationSetService.findOneByDocumentId(documentId))
            .thenThrow(new DataIntegrityViolationException("Data integrity violation"));

        ResponseEntity<AnnotationSetDTO> response = filterAnnotationSet.getAllAnnotationSets(documentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}