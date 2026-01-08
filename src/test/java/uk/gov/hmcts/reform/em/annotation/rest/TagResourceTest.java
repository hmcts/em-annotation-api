package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.annotation.service.TagService;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagResourceTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagResource tagResource;

    @Test
    void getTagsCreatedBySuccess() {
        String createdBy = "user123";
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("Test Tag");
        List<TagDTO> tags = List.of(tagDTO);

        when(tagService.findTagByCreatedBy(createdBy)).thenReturn(tags);

        ResponseEntity<List<TagDTO>> response = tagResource.getTagsCreatedBy(createdBy);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst()).isEqualTo(tagDTO);
        
        verify(tagService).findTagByCreatedBy(createdBy);
    }

    @Test
    void getTagsCreatedByReturnsEmptyListWhenNoTagsFound() {
        String createdBy = "userWithNoTags";
        when(tagService.findTagByCreatedBy(createdBy)).thenReturn(Collections.emptyList());

        ResponseEntity<List<TagDTO>> response = tagResource.getTagsCreatedBy(createdBy);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        
        verify(tagService).findTagByCreatedBy(createdBy);
    }
}