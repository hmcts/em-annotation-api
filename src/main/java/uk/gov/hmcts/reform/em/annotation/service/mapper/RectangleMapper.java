package uk.gov.hmcts.reform.em.annotation.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;

import java.util.UUID;

/**
 * Mapper for the entity Rectangle and its DTO RectangleDTO.
 */
@Mapper(componentModel = "spring", uses = {AnnotationMapper.class})
public interface RectangleMapper extends EntityMapper<RectangleDTO, Rectangle> {

    @Mapping(source = "annotation.id", target = "annotationId")
    RectangleDTO toDto(Rectangle rectangle);

    @Mapping(source = "annotationId", target = "annotation")
    Rectangle toEntity(RectangleDTO rectangleDTO);

    default Rectangle fromId(UUID id) {
        if (id == null) {
            return null;
        }
        Rectangle rectangle = new Rectangle();
        rectangle.setId(id);
        return rectangle;
    }
}
