package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.service.CommentService;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentDTO;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentResourceTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentResource commentResource;

    private CommentDTO commentDTO;
    private UUID commentId;

    private static final String ENTITY_CREATION_ALERT = "X-emannotationapp-alert";
    private static final String ENTITY_UPDATE_ALERT = "X-emannotationapp-alert";
    private static final String ENTITY_DELETION_ALERT = "X-emannotationapp-alert";

    @BeforeEach
    void setUp() {
        commentId = UUID.randomUUID();
        commentDTO = new CommentDTO();
        commentDTO.setId(commentId);
        commentDTO.setAnnotationId(UUID.randomUUID());
        commentDTO.setContent("Test Comment");
    }

    @Test
    void createCommentSuccess() throws URISyntaxException {
        when(commentService.save(any(CommentDTO.class))).thenReturn(commentDTO);

        ResponseEntity<CommentDTO> response = commentResource.createComment(commentDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(commentDTO);
        assertThat(response.getHeaders().getFirst(ENTITY_CREATION_ALERT)).isNotNull();
        assertThat(response.getHeaders().getLocation()).hasPath("/api/comments/" + commentId);

        verify(commentService).save(commentDTO);
    }

    @Test
    void createCommentThrowsBadRequestWhenIdIsNull() {
        commentDTO.setId(null);

        assertThatThrownBy(() -> commentResource.createComment(commentDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid id");
    }

    @Test
    void createCommentThrowsBadRequestWhenAnnotationIdIsNull() {
        commentDTO.setAnnotationId(null);

        assertThatThrownBy(() -> commentResource.createComment(commentDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid annotation id");
    }

    @Test
    void updateCommentSuccess() throws URISyntaxException {
        when(commentService.save(any(CommentDTO.class))).thenReturn(commentDTO);

        ResponseEntity<CommentDTO> response = commentResource.updateComment(commentDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(commentDTO);
        assertThat(response.getHeaders().getFirst(ENTITY_UPDATE_ALERT)).isNotNull();

        verify(commentService).save(commentDTO);
    }

    @Test
    void updateCommentThrowsBadRequestWhenIdIsNull() {
        commentDTO.setId(null);

        assertThatThrownBy(() -> commentResource.updateComment(commentDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid id");
    }

    @Test
    void updateCommentThrowsBadRequestWhenAnnotationIdIsNull() {
        commentDTO.setAnnotationId(null);

        assertThatThrownBy(() -> commentResource.updateComment(commentDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid annotation id");
    }

    @Test
    void getAllCommentsSuccess() {
        Pageable pageable = Pageable.unpaged();
        Page<CommentDTO> page = new PageImpl<>(List.of(commentDTO));
        when(commentService.findAll(pageable)).thenReturn(page);

        ResponseEntity<List<CommentDTO>> response = commentResource.getAllComments(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(commentDTO);
        assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");
    }

    @Test
    void getCommentSuccess() {
        when(commentService.findOne(commentId)).thenReturn(Optional.of(commentDTO));

        ResponseEntity<CommentDTO> response = commentResource.getComment(commentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(commentDTO);
    }

    @Test
    void getCommentReturnsNotFound() {
        when(commentService.findOne(commentId)).thenReturn(Optional.empty());

        ResponseEntity<CommentDTO> response = commentResource.getComment(commentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteCommentSuccess() {
        ResponseEntity<Void> response = commentResource.deleteComment(commentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(ENTITY_DELETION_ALERT)).isNotNull();

        verify(commentService).delete(commentId);
    }
}