package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import static org.mockito.Mockito.*;

public class MetadataServiceImplTest {

    @InjectMocks
    private MetadataServiceImpl metadataService;

    @Mock
    private MetadataRepository metadataRepository;

    @Mock
    private MetadataMapper metadataMapper;

    @Mock
    private SecurityUtils securityUtils;

    private UUID documentId =  UUID.randomUUID();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveSuccessCreate() {

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


        Assert.assertEquals(metadataDto.getDocumentId(), updatedDto.getDocumentId());
        Assert.assertEquals(metadataDto.getRotationAngle(), updatedDto.getRotationAngle());

    }

    @Test
    public void testSaveSuccessUpdate() {

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


        Assert.assertEquals(metadataDto.getDocumentId(), updatedDto.getDocumentId());
        Assert.assertEquals(metadataDto.getRotationAngle(), updatedDto.getRotationAngle());

    }

    @Test(expected = BadCredentialsException.class)
    public void testSaveFailure() {
        MetadataDto metadataDto = createMetadataDto();
        metadataService.save(metadataDto);
    }

    @Test
    public void testFindByDocumentIdSuccess() {

        MetadataDto metadataDto = createMetadataDto();
        Metadata metadata = createMetadata();

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testuser"));
        when(metadataRepository.findByDocumentId(metadataDto.getDocumentId()))
            .thenReturn(metadata);
        when(metadataMapper.toDto(metadata)).thenReturn(metadataDto);

        MetadataDto updatedDto = metadataService.findByDocumentId(metadataDto.getDocumentId());

        verify(metadataRepository, atLeast(1))
            .findByDocumentId(metadataDto.getDocumentId());

        Assert.assertEquals(metadataDto.getDocumentId(), updatedDto.getDocumentId());
        Assert.assertEquals(metadataDto.getRotationAngle(), updatedDto.getRotationAngle());
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
