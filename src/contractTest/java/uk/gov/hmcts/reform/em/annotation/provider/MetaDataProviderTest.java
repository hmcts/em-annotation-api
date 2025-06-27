package uk.gov.hmcts.reform.em.annotation.provider;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.em.annotation.rest.MetaDataResource;
import uk.gov.hmcts.reform.em.annotation.service.MetadataService;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Provider("annotation_api_metadata_provider")
@WebMvcTest(value = MetaDataResource.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class
})
public class MetaDataProviderTest extends BaseProviderTest {

    @Autowired
    private MetaDataResource metaDataResource;

    @MockitoBean
    private MetadataService metadataService;

    private static final UUID EXAMPLE_DOCUMENT_ID = UUID.fromString("8c53579b-d935-4204-82c8-250329c29d91");
    private static final Integer EXAMPLE_ROTATION_ANGLE = 90;

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[]{metaDataResource};
    }

    @State("metadata can be created for a document")
    public void createMetaDataState() {
        MetadataDto metadataDto = createMetaDataDTO();
        when(metadataService.save(any(MetadataDto.class))).thenReturn(metadataDto);
    }

    @State("metadata exists for a document")
    public void getMetaDataState() {
        MetadataDto metadataDto = createMetaDataDTO();
        when(metadataService.findByDocumentId(EXAMPLE_DOCUMENT_ID)).thenReturn(metadataDto);
    }

    private MetadataDto createMetaDataDTO() {
        MetadataDto dto = new MetadataDto();
        dto.setRotationAngle(EXAMPLE_ROTATION_ANGLE);
        dto.setDocumentId(EXAMPLE_DOCUMENT_ID);
        return dto;
    }
}