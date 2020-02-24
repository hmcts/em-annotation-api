package uk.gov.hmcts.reform.em.annotation.service.mapper;

import org.mapstruct.Mapper;
import uk.gov.hmcts.reform.em.annotation.domain.CommentTag;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentTagDTO;

/**
 * Mapper for the entity Comment Tag and its DTO CommentTagDTO.
 */
@Mapper(componentModel = "spring")
public interface CommentTagMapper extends EntityMapper<CommentTagDTO, CommentTag> {
}
