package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Metadata;
import uk.gov.hmcts.reform.em.annotation.repository.MetadataRepository;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;
import uk.gov.hmcts.reform.em.annotation.service.mapper.MetadataMapper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MetadataServiceImplTest {

    @InjectMocks
    private MetadataServiceImpl metadataService;

    @Mock
    private MetadataRepository metadataRepository;

    @Mock
    private MetadataMapper metadataMapper;

    @Mock
    private SecurityUtils securityUtils;

    private UUID documentId =  UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveSuccessCreate() {

        MetadataDto metadataDto = createMetadataDto();
        Metadata metadata = createMetadata();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testuser"));
        when(metadataMapper.toEntity(metadataDto)).thenReturn(metadata);
        when(metadataMapper.toDto(metadata)).thenReturn(metadataDto);
        when(metadataRepository.save(metadata)).thenReturn(metadata);
        when(metadataRepository.findByDocumentId(metadataDto.getDocumentId())).thenReturn(null);

        MetadataDto updatedDto = metadataService.save(metadataDto);
        assertThat(updatedDto).isNotNull();
        verify(metadataRepository, atLeast(1)).save(metadata);
        verify(metadataMapper, atLeast(1)).toEntity(metadataDto);
        verify(metadataMapper, atLeast(1)).toDto(metadata);


        Assertions.assertEquals(metadataDto.getDocumentId(), updatedDto.getDocumentId());
        Assertions.assertEquals(metadataDto.getRotationAngle(), updatedDto.getRotationAngle());

    }

    @Test
    void testSaveSuccessUpdate() {

        MetadataDto metadataDto = createMetadataDto();
        Metadata metadata = createMetadata();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testuser"));
        when(metadataMapper.toEntity(metadataDto)).thenReturn(metadata);
        when(metadataMapper.toDto(metadata)).thenReturn(metadataDto);
        when(metadataRepository.save(metadata)).thenReturn(metadata);
        when(metadataRepository.findByDocumentId(metadataDto.getDocumentId())).thenReturn(metadata);

        MetadataDto updatedDto = metadataService.save(metadataDto);
        assertThat(updatedDto).isNotNull();
        verify(metadataRepository, atLeast(1)).save(metadata);
        verify(metadataMapper, atLeast(0)).toEntity(metadataDto);
        verify(metadataMapper, atLeast(1)).toDto(metadata);


        Assertions.assertEquals(metadataDto.getDocumentId(), updatedDto.getDocumentId());
        Assertions.assertEquals(metadataDto.getRotationAngle(), updatedDto.getRotationAngle());

    }

    @Test
    void testSaveFailure() {
        MetadataDto metadataDto = createMetadataDto();
        Assertions.assertThrows(BadCredentialsException.class, () -> metadataService.save(metadataDto));
    }

    @Test
    void testFindByDocumentIdSuccess() {

        MetadataDto metadataDto = createMetadataDto();
        Metadata metadata = createMetadata();

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testuser"));
        when(metadataRepository.findByDocumentId(metadataDto.getDocumentId()))
            .thenReturn(metadata);
        when(metadataMapper.toDto(metadata)).thenReturn(metadataDto);

        MetadataDto updatedDto = metadataService.findByDocumentId(metadataDto.getDocumentId());

        verify(metadataRepository, atLeast(1))
            .findByDocumentId(metadataDto.getDocumentId());

        Assertions.assertEquals(metadataDto.getDocumentId(), updatedDto.getDocumentId());
        Assertions.assertEquals(metadataDto.getRotationAngle(), updatedDto.getRotationAngle());
    }

    private MetadataDto createMetadataDto() {

        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setRotationAngle(90);
        metadataDto.setDocumentId(documentId);

        return metadataDto;
    }

    private Metadata createMetadata() {
        Metadata metadata = new Metadata();
        metadata.setRotationAngle(90);
        metadata.setDocumentId(documentId);

        return metadata;
    }
}
