package uk.gov.hmcts.reform.em.annotation.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;

/**
 * Mapper for the entity Tag and its DTO TagDTO.
 */
@Mapper(componentModel = "spring", uses = {AnnotationMapper.class})
public interface TagMapper extends EntityMapper<TagDTO, Tag> {

    @Mapping(source = "annotation.id", target = "annotationId")
    TagDTO toDto(Tag tag);

    @Mapping(source = "annotationId", target = "annotation")
    Tag toEntity(TagDTO tagDTO);
}
