package uk.gov.hmcts.reform.em.annotation.service.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.domain.Comment;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-10-31T17:25:25+0000",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 1.3.1100.v20200828-0941, environment: Java 15 (Oracle Corporation)"
)
@Component
public class AnnotationMapperImpl implements AnnotationMapper {

    @Autowired
    private AnnotationSetMapper annotationSetMapper;
    @Autowired
    private RectangleMapper rectangleMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Annotation> toEntity(List<AnnotationDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Annotation> list = new ArrayList<Annotation>( dtoList.size() );
        for ( AnnotationDTO annotationDTO : dtoList ) {
            list.add( toEntity( annotationDTO ) );
        }

        return list;
    }

    @Override
    public List<AnnotationDTO> toDto(List<Annotation> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<AnnotationDTO> list = new ArrayList<AnnotationDTO>( entityList.size() );
        for ( Annotation annotation : entityList ) {
            list.add( toDto( annotation ) );
        }

        return list;
    }

    @Override
    public AnnotationDTO toDto(Annotation annotation) {
        if ( annotation == null ) {
            return null;
        }

        AnnotationDTO annotationDTO = new AnnotationDTO();

        UUID id = annotationAnnotationSetId( annotation );
        if ( id != null ) {
            annotationDTO.setAnnotationSetId( id );
        }
        annotationDTO.setCreatedBy( annotation.getCreatedBy() );
        annotationDTO.setCreatedDate( annotation.getCreatedDate() );
        annotationDTO.setLastModifiedBy( annotation.getLastModifiedBy() );
        annotationDTO.setLastModifiedDate( annotation.getLastModifiedDate() );
        annotationDTO.setCreatedByDetails( annotation.getCreatedByDetails() );
        annotationDTO.setLastModifiedByDetails( annotation.getLastModifiedByDetails() );
        annotationDTO.setId( annotation.getId() );
        annotationDTO.setAnnotationType( annotation.getAnnotationType() );
        annotationDTO.setPage( annotation.getPage() );
        annotationDTO.setComments( commentSetToCommentDTOSet( annotation.getComments() ) );
        annotationDTO.setTags( tagSetToTagDTOSet( annotation.getTags() ) );
        annotationDTO.setRectangles( rectangleSetToRectangleDTOSet( annotation.getRectangles() ) );
        annotationDTO.setColor( annotation.getColor() );

        return annotationDTO;
    }

    @Override
    public Annotation toEntity(AnnotationDTO annotationDTO) {
        if ( annotationDTO == null ) {
            return null;
        }

        Annotation annotation = new Annotation();

        annotation.setAnnotationSet( annotationSetMapper.fromId( annotationDTO.getAnnotationSetId() ) );
        annotation.setCreatedBy( annotationDTO.getCreatedBy() );
        annotation.setCreatedDate( annotationDTO.getCreatedDate() );
        annotation.setLastModifiedBy( annotationDTO.getLastModifiedBy() );
        annotation.setLastModifiedDate( annotationDTO.getLastModifiedDate() );
        annotation.setColor( annotationDTO.getColor() );
        annotation.setId( annotationDTO.getId() );
        annotation.setAnnotationType( annotationDTO.getAnnotationType() );
        annotation.setPage( annotationDTO.getPage() );
        annotation.setComments( commentDTOSetToCommentSet( annotationDTO.getComments() ) );
        annotation.setRectangles( rectangleDTOSetToRectangleSet( annotationDTO.getRectangles() ) );
        if ( annotation.getTags() != null ) {
            Set<Tag> set2 = tagDTOSetToTagSet( annotationDTO.getTags() );
            if ( set2 != null ) {
                annotation.getTags().addAll( set2 );
            }
        }

        return annotation;
    }

    private UUID annotationAnnotationSetId(Annotation annotation) {
        if ( annotation == null ) {
            return null;
        }
        AnnotationSet annotationSet = annotation.getAnnotationSet();
        if ( annotationSet == null ) {
            return null;
        }
        UUID id = annotationSet.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected Set<CommentDTO> commentSetToCommentDTOSet(Set<Comment> set) {
        if ( set == null ) {
            return null;
        }

        Set<CommentDTO> set1 = new HashSet<CommentDTO>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Comment comment : set ) {
            set1.add( commentMapper.toDto( comment ) );
        }

        return set1;
    }

    protected Set<TagDTO> tagSetToTagDTOSet(Set<Tag> set) {
        if ( set == null ) {
            return null;
        }

        Set<TagDTO> set1 = new HashSet<TagDTO>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Tag tag : set ) {
            set1.add( tagMapper.toDto( tag ) );
        }

        return set1;
    }

    protected Set<RectangleDTO> rectangleSetToRectangleDTOSet(Set<Rectangle> set) {
        if ( set == null ) {
            return null;
        }

        Set<RectangleDTO> set1 = new HashSet<RectangleDTO>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Rectangle rectangle : set ) {
            set1.add( rectangleMapper.toDto( rectangle ) );
        }

        return set1;
    }

    protected Set<Comment> commentDTOSetToCommentSet(Set<CommentDTO> set) {
        if ( set == null ) {
            return null;
        }

        Set<Comment> set1 = new HashSet<Comment>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( CommentDTO commentDTO : set ) {
            set1.add( commentMapper.toEntity( commentDTO ) );
        }

        return set1;
    }

    protected Set<Rectangle> rectangleDTOSetToRectangleSet(Set<RectangleDTO> set) {
        if ( set == null ) {
            return null;
        }

        Set<Rectangle> set1 = new HashSet<Rectangle>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( RectangleDTO rectangleDTO : set ) {
            set1.add( rectangleMapper.toEntity( rectangleDTO ) );
        }

        return set1;
    }

    protected Set<Tag> tagDTOSetToTagSet(Set<TagDTO> set) {
        if ( set == null ) {
            return null;
        }

        Set<Tag> set1 = new HashSet<Tag>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( TagDTO tagDTO : set ) {
            set1.add( tagMapper.toEntity( tagDTO ) );
        }

        return set1;
    }
}
