package uk.gov.hmcts.reform.em.annotation.provider;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.em.annotation.rest.TagResource;
import uk.gov.hmcts.reform.em.annotation.service.TagService;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Provider("annotation_api_tag_provider")
@WebMvcTest(value = TagResource.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class
})
public class TagProviderTest extends BaseProviderTest {

    @Autowired
    private TagResource tagResource;

    @MockitoBean
    private TagService tagService;

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[]{tagResource};
    }

    @State({"tags exist for a user"})
    public void getTagsForUser() {
        TagDTO tagDTO = createTagDTO();
        when(tagService.findTagByCreatedBy(anyString()))
            .thenReturn(List.of(tagDTO));
    }

    private TagDTO createTagDTO() {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("Review");
        tagDTO.setCreatedBy(EXAMPLE_USER_ID.toString());
        tagDTO.setLabel("For Review");
        tagDTO.setColor("ff0000");
        return tagDTO;
    }
}