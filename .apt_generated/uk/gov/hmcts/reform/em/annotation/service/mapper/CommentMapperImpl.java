package uk.gov.hmcts.reform.em.annotation.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.Comment;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-10-31T17:25:25+0000",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 1.3.1100.v20200828-0941, environment: Java 15 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Autowired
    private AnnotationMapper annotationMapper;

    @Override
    public List<Comment> toEntity(List<CommentDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Comment> list = new ArrayList<Comment>( dtoList.size() );
        for ( CommentDTO commentDTO : dtoList ) {
            list.add( toEntity( commentDTO ) );
        }

        return list;
    }

    @Override
    public List<CommentDTO> toDto(List<Comment> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<CommentDTO> list = new ArrayList<CommentDTO>( entityList.size() );
        for ( Comment comment : entityList ) {
            list.add( toDto( comment ) );
        }

        return list;
    }

    @Override
    public CommentDTO toDto(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentDTO commentDTO = new CommentDTO();

        UUID id = commentAnnotationId( comment );
        if ( id != null ) {
            commentDTO.setAnnotationId( id );
        }
        commentDTO.setCreatedBy( comment.getCreatedBy() );
        commentDTO.setCreatedDate( comment.getCreatedDate() );
        commentDTO.setLastModifiedBy( comment.getLastModifiedBy() );
        commentDTO.setLastModifiedDate( comment.getLastModifiedDate() );
        commentDTO.setCreatedByDetails( comment.getCreatedByDetails() );
        commentDTO.setLastModifiedByDetails( comment.getLastModifiedByDetails() );
        commentDTO.setId( comment.getId() );
        commentDTO.setContent( comment.getContent() );

        return commentDTO;
    }

    @Override
    public Comment toEntity(CommentDTO commentDTO) {
        if ( commentDTO == null ) {
            return null;
        }

        Comment comment = new Comment();

        comment.setAnnotation( annotationMapper.fromId( commentDTO.getAnnotationId() ) );
        comment.setCreatedBy( commentDTO.getCreatedBy() );
        comment.setCreatedDate( commentDTO.getCreatedDate() );
        comment.setLastModifiedBy( commentDTO.getLastModifiedBy() );
        comment.setLastModifiedDate( commentDTO.getLastModifiedDate() );
        comment.setId( commentDTO.getId() );
        comment.setContent( commentDTO.getContent() );

        return comment;
    }

    private UUID commentAnnotationId(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        Annotation annotation = comment.getAnnotation();
        if ( annotation == null ) {
            return null;
        }
        UUID id = annotation.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
