package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.annotation.service.MetadataService;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetaDataResourceTest {

    @Mock
    private MetadataService metadataService;

    @InjectMocks
    private MetaDataResource metaDataResource;

    private MetadataDto metadataDto;
    private UUID documentId;

    private static final String ENTITY_CREATION_ALERT = "X-emannotationapp-alert";

    @BeforeEach
    void setUp() {
        documentId = UUID.randomUUID();
        metadataDto = new MetadataDto();
        metadataDto.setDocumentId(documentId);
        metadataDto.setRotationAngle(90);
    }

    @Test
    void createMetaDataSuccess() throws URISyntaxException {
        when(metadataService.save(any(MetadataDto.class))).thenReturn(metadataDto);

        ResponseEntity<MetadataDto> response = metaDataResource.createMetaData(metadataDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(metadataDto);
        assertThat(response.getHeaders().getFirst(ENTITY_CREATION_ALERT)).isNotNull();
        assertThat(response.getHeaders().getLocation()).hasPath("/api/metadata/" + documentId);

        verify(metadataService).save(metadataDto);
    }

    @Test
    void getMetadataSuccess() {
        when(metadataService.findByDocumentId(documentId)).thenReturn(metadataDto);

        ResponseEntity<MetadataDto> response = metaDataResource.getMetadata(documentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(metadataDto);
    }

    @Test
    void getMetadataReturnsNoContentWhenDtoIsNull() {
        when(metadataService.findByDocumentId(documentId)).thenReturn(null);

        ResponseEntity<MetadataDto> response = metaDataResource.getMetadata(documentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getMetadataReturnsNoContentWhenRotationAngleIsNull() {
        metadataDto.setRotationAngle(null);
        when(metadataService.findByDocumentId(documentId)).thenReturn(metadataDto);

        ResponseEntity<MetadataDto> response = metaDataResource.getMetadata(documentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}