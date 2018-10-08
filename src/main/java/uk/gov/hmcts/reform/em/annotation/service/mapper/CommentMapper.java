package uk.gov.hmcts.reform.em.annotation.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.hmcts.reform.em.annotation.domain.Comment;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentDTO;

import java.util.UUID;

/**
 * Mapper for the entity Comment and its DTO CommentDTO.
 */
@Mapper(componentModel = "spring", uses = {AnnotationMapper.class})
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {

    @Mapping(source = "annotation.id", target = "annotationId")
    CommentDTO toDto(Comment comment);

    @Mapping(source = "annotationId", target = "annotation")
    @Mapping(target = "createdByDetails", ignore = true)
    @Mapping(target = "lastModifiedByDetails", ignore = true)
    Comment toEntity(CommentDTO commentDTO);

    default Comment fromId(UUID id) {
        if (id == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(id);
        return comment;
    }
}
