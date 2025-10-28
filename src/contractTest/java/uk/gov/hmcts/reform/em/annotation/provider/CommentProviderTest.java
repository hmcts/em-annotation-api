package uk.gov.hmcts.reform.em.annotation.provider;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.em.annotation.rest.CommentResource;
import uk.gov.hmcts.reform.em.annotation.service.CommentService;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Provider("annotation_api_comment_provider")
@WebMvcTest(value = CommentResource.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class
})
public class CommentProviderTest extends BaseProviderTest {

    private final CommentResource commentResource;

    @MockitoBean
    private CommentService commentService;

    private static final UUID EXAMPLE_COMMENT_ID = UUID.fromString("b3438f7d-0275-4063-9524-1a6d0b68636b");
    private static final UUID EXAMPLE_ANNOTATION_ID = UUID.fromString("a58e5f39-2b0f-48e2-b052-e932375b4f69");

    @Autowired
    public CommentProviderTest(MockMvc mockMvc, ObjectMapper objectMapper, CommentResource commentResource) {
        super(mockMvc, objectMapper);
        this.commentResource = commentResource;
    }

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[]{commentResource};
    }

    @State({"comment is created successfully", "comment is updated successfully"})
    public void createOrUpdateComment() {
        CommentDTO commentDto = createCommentDto();
        when(commentService.save(any(CommentDTO.class))).thenReturn(commentDto);
    }

    @State({"comments exist"})
    public void getAllComments() {
        CommentDTO comment1 = createCommentDto();
        CommentDTO comment2 = createCommentDto();
        comment2.setId(UUID.randomUUID());

        Page<CommentDTO> page = new PageImpl<>(List.of(comment1, comment2));
        when(commentService.findAll(any(Pageable.class))).thenReturn(page);
    }

    @State({"a comment exists with the given id"})
    public void getCommentById() {
        CommentDTO commentDto = createCommentDto();
        when(commentService.findOne(EXAMPLE_COMMENT_ID)).thenReturn(Optional.of(commentDto));
    }

    @State({"a comment exists for deletion"})
    public void deleteComment() {
        doNothing().when(commentService).delete(EXAMPLE_COMMENT_ID);
    }

    private CommentDTO createCommentDto() {
        CommentDTO dto = new CommentDTO();
        dto.setId(EXAMPLE_COMMENT_ID);
        dto.setAnnotationId(EXAMPLE_ANNOTATION_ID);
        dto.setContent("This is a sample comment.");

        dto.setCreatedBy(EXAMPLE_USER_ID.toString());
        dto.setCreatedDate(EXAMPLE_DATE);
        dto.setCreatedByDetails(createIdamDetails());
        dto.setLastModifiedBy(EXAMPLE_USER_ID.toString());
        dto.setLastModifiedDate(EXAMPLE_DATE);
        dto.setLastModifiedByDetails(createIdamDetails());

        return dto;
    }
}