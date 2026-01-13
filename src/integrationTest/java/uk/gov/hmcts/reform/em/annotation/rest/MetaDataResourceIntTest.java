package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import uk.gov.hmcts.reform.em.annotation.service.MetadataService;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;

class MetaDataResourceIntTest {

    @InjectMocks
    private MetaDataResource metaDataResource;

    @Mock
    private MetadataService metadataService;

    @Mock
    private WebDataBinder webDataBinder;

    private final UUID documentId =  UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMetaDataSuccess() throws URISyntaxException {

        MetadataDto metadataDto = createMetadataDto();
        Mockito.when(metadataService.save(metadataDto)).thenReturn(metadataDto);

        ResponseEntity<MetadataDto> responseEntity = metaDataResource.createMetaData(metadataDto);

        MetadataDto response = responseEntity.getBody();
        assertEquals(metadataDto.getDocumentId(), response.getDocumentId());
        assertEquals(metadataDto.getRotationAngle(), response.getRotationAngle());

        Mockito.verify(metadataService, Mockito.atLeast(1)).save(metadataDto);
    }

    @Test
    void getMetadataSuccess() {
        MetadataDto metadataDto = createMetadataDto();
        Mockito.when(metadataService.findByDocumentId(metadataDto.getDocumentId())).thenReturn(metadataDto);

        metaDataResource.getMetadata(metadataDto.getDocumentId());

        Mockito.verify(metadataService, Mockito.atLeast(1)).findByDocumentId(metadataDto.getDocumentId());
    }

    @Test
    void getMetaDataFailure() {

        MetadataDto metadataDto = new MetadataDto();
        Mockito.when(metadataService.findByDocumentId(metadataDto.getDocumentId())).thenReturn(metadataDto);

        ResponseEntity<MetadataDto> responseEntity = metaDataResource.getMetadata(metadataDto.getDocumentId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        Mockito.verify(metadataService, Mockito.atLeast(1)).findByDocumentId(metadataDto.getDocumentId());
    }

    @Test
    void getMetaDataFailure2() {

        MetadataDto metadataDto = new MetadataDto();
        Mockito.when(metadataService.findByDocumentId(metadataDto.getDocumentId())).thenReturn(null);

        ResponseEntity<MetadataDto> responseEntity = metaDataResource.getMetadata(metadataDto.getDocumentId());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        Mockito.verify(metadataService, Mockito.atLeast(1)).findByDocumentId(metadataDto.getDocumentId());
    }

    @Test
    void testInitBinder() {
        metaDataResource.initBinder(webDataBinder);
        Mockito.verify(webDataBinder, Mockito.atLeast(1)).setDisallowedFields(anyString());
    }

    private MetadataDto createMetadataDto() {

        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setRotationAngle(90);
        metadataDto.setDocumentId(documentId);

        return metadataDto;
    }

}
