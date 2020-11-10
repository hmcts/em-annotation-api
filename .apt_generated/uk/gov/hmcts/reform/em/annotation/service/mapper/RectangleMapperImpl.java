package uk.gov.hmcts.reform.em.annotation.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-10-31T17:25:26+0000",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 1.3.1100.v20200828-0941, environment: Java 15 (Oracle Corporation)"
)
@Component
public class RectangleMapperImpl implements RectangleMapper {

    @Autowired
    private AnnotationMapper annotationMapper;

    @Override
    public List<Rectangle> toEntity(List<RectangleDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Rectangle> list = new ArrayList<Rectangle>( dtoList.size() );
        for ( RectangleDTO rectangleDTO : dtoList ) {
            list.add( toEntity( rectangleDTO ) );
        }

        return list;
    }

    @Override
    public List<RectangleDTO> toDto(List<Rectangle> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<RectangleDTO> list = new ArrayList<RectangleDTO>( entityList.size() );
        for ( Rectangle rectangle : entityList ) {
            list.add( toDto( rectangle ) );
        }

        return list;
    }

    @Override
    public RectangleDTO toDto(Rectangle rectangle) {
        if ( rectangle == null ) {
            return null;
        }

        RectangleDTO rectangleDTO = new RectangleDTO();

        UUID id = rectangleAnnotationId( rectangle );
        if ( id != null ) {
            rectangleDTO.setAnnotationId( id );
        }
        rectangleDTO.setCreatedBy( rectangle.getCreatedBy() );
        rectangleDTO.setCreatedDate( rectangle.getCreatedDate() );
        rectangleDTO.setLastModifiedBy( rectangle.getLastModifiedBy() );
        rectangleDTO.setLastModifiedDate( rectangle.getLastModifiedDate() );
        rectangleDTO.setCreatedByDetails( rectangle.getCreatedByDetails() );
        rectangleDTO.setLastModifiedByDetails( rectangle.getLastModifiedByDetails() );
        rectangleDTO.setId( rectangle.getId() );
        rectangleDTO.setX( rectangle.getX() );
        rectangleDTO.setY( rectangle.getY() );
        rectangleDTO.setWidth( rectangle.getWidth() );
        rectangleDTO.setHeight( rectangle.getHeight() );

        return rectangleDTO;
    }

    @Override
    public Rectangle toEntity(RectangleDTO rectangleDTO) {
        if ( rectangleDTO == null ) {
            return null;
        }

        Rectangle rectangle = new Rectangle();

        rectangle.setAnnotation( annotationMapper.fromId( rectangleDTO.getAnnotationId() ) );
        rectangle.setCreatedBy( rectangleDTO.getCreatedBy() );
        rectangle.setCreatedDate( rectangleDTO.getCreatedDate() );
        rectangle.setLastModifiedBy( rectangleDTO.getLastModifiedBy() );
        rectangle.setLastModifiedDate( rectangleDTO.getLastModifiedDate() );
        rectangle.setId( rectangleDTO.getId() );
        rectangle.setX( rectangleDTO.getX() );
        rectangle.setY( rectangleDTO.getY() );
        rectangle.setWidth( rectangleDTO.getWidth() );
        rectangle.setHeight( rectangleDTO.getHeight() );

        return rectangle;
    }

    private UUID rectangleAnnotationId(Rectangle rectangle) {
        if ( rectangle == null ) {
            return null;
        }
        Annotation annotation = rectangle.getAnnotation();
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
