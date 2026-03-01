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
import uk.gov.hmcts.reform.em.annotation.rest.BookmarkResource;
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Provider("annotation_api_bookmark_provider")
@WebMvcTest(value = BookmarkResource.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class
})
public class BookmarkProviderTest extends BaseProviderTest {

    private final BookmarkResource bookmarkResource;

    @MockitoBean
    private BookmarkService bookmarkService;

    private static final UUID EXAMPLE_BOOKMARK_ID = UUID.fromString("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");
    private static final UUID ANOTHER_EXAMPLE_BOOKMARK_ID = UUID.fromString("5a6b7c8d-9e0f-1a2b-3c4d-5e6f7a8b9c0a");
    private static final UUID EXAMPLE_DOCUMENT_ID = UUID.fromString("2a3b4c5d-6e7f-8a9b-0c1d-2e3f4a5b6c7d");
    private static final UUID EXAMPLE_PARENT_BOOKMARK_ID = UUID.fromString("3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d");
    private static final UUID EXAMPLE_PREVIOUS_BOOKMARK_ID = UUID.fromString("4a5b6c7d-8e9f-0a1b-2c3d-4e5f6a7b8c9d");
    public static final String EXAMPLE_BOOKMARK_NAME = "My Important Bookmark";

    @Autowired
    public BookmarkProviderTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            BookmarkResource bookmarkResource) {
        super(mockMvc, objectMapper);
        this.bookmarkResource = bookmarkResource;
    }


    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[]{bookmarkResource};
    }

    @State({"bookmark is created successfully"})
    public void createBookmark() {
        BookmarkDTO bookmarkDto = createBookmarkDTO(EXAMPLE_BOOKMARK_ID, EXAMPLE_BOOKMARK_NAME, 5);
        when(bookmarkService.save(any(BookmarkDTO.class))).thenReturn(bookmarkDto);
    }

    @State({"bookmark is updated successfully"})
    public void updateBookmark() {
        BookmarkDTO bookmarkDto = createBookmarkDTO(EXAMPLE_BOOKMARK_ID, EXAMPLE_BOOKMARK_NAME, 5);
        when(bookmarkService.update(any(BookmarkDTO.class))).thenReturn(bookmarkDto);
    }

    @State({"bookmarks are updated successfully"})
    public void updateMultipleBookmarks() {
        BookmarkDTO bookmark1 = createBookmarkDTO(EXAMPLE_BOOKMARK_ID, EXAMPLE_BOOKMARK_NAME, 5);
        BookmarkDTO bookmark2 = createBookmarkDTO(ANOTHER_EXAMPLE_BOOKMARK_ID, "Another Bookmark", 10);

        when(bookmarkService.update(argThat(dto ->
            Objects.nonNull(dto) && dto.getId().equals(EXAMPLE_BOOKMARK_ID)))).thenReturn(bookmark1);
        when(bookmarkService.update(argThat(dto ->
            Objects.nonNull(dto) && dto.getId().equals(ANOTHER_EXAMPLE_BOOKMARK_ID)))).thenReturn(bookmark2);
    }

    @State({"bookmarks exist for a document"})
    public void getBookmarksForDocument() {
        BookmarkDTO bookmark1 = createBookmarkDTO(EXAMPLE_BOOKMARK_ID, EXAMPLE_BOOKMARK_NAME, 5);
        BookmarkDTO bookmark2 = createBookmarkDTO(ANOTHER_EXAMPLE_BOOKMARK_ID, "Another Bookmark", 10);
        List<BookmarkDTO> bookmarks = List.of(bookmark1, bookmark2);
        Page<BookmarkDTO> page = new PageImpl<>(bookmarks);
        when(bookmarkService.findAllByDocumentId(any(UUID.class), any(Pageable.class))).thenReturn(page);
    }

    @State({"bookmark exists for deletion"})
    public void deleteBookmark() {
        doNothing().when(bookmarkService).delete(EXAMPLE_BOOKMARK_ID);
    }

    @State({"bookmarks exist for multiple deletion"})
    public void deleteMultipleBookmarks() {
        doNothing().when(bookmarkService).deleteAllById(anyList());

        BookmarkDTO updatedBookmark = createBookmarkDTO(EXAMPLE_PARENT_BOOKMARK_ID, "Updated Parent Bookmark", 3);
        when(bookmarkService.save(argThat(dto ->
            Objects.nonNull(dto) && dto.getId().equals(EXAMPLE_PARENT_BOOKMARK_ID)))).thenReturn(updatedBookmark);
    }

    private BookmarkDTO createBookmarkDTO(UUID id, String name, int pageNumber) {
        BookmarkDTO dto = new BookmarkDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setDocumentId(EXAMPLE_DOCUMENT_ID);
        dto.setCreatedBy(EXAMPLE_USER_ID.toString());
        dto.setPageNumber(pageNumber);
        dto.setxCoordinate(100.5);
        dto.setyCoordinate(200.75);
        dto.setParent(EXAMPLE_PARENT_BOOKMARK_ID);
        dto.setPrevious(EXAMPLE_PREVIOUS_BOOKMARK_ID);
        return dto;
    }
}