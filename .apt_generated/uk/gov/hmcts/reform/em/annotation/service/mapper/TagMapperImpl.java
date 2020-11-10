package uk.gov.hmcts.reform.em.annotation.service.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-10-31T17:25:26+0000",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 1.3.1100.v20200828-0941, environment: Java 15 (Oracle Corporation)"
)
@Component
public class TagMapperImpl implements TagMapper {

    @Override
    public Tag toEntity(TagDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Tag tag = new Tag();

        tag.setName( dto.getName() );
        tag.setCreatedBy( dto.getCreatedBy() );
        tag.setLabel( dto.getLabel() );
        tag.setColor( dto.getColor() );

        return tag;
    }

    @Override
    public TagDTO toDto(Tag entity) {
        if ( entity == null ) {
            return null;
        }

        TagDTO tagDTO = new TagDTO();

        tagDTO.setName( entity.getName() );
        tagDTO.setCreatedBy( entity.getCreatedBy() );
        tagDTO.setLabel( entity.getLabel() );
        tagDTO.setColor( entity.getColor() );

        return tagDTO;
    }

    @Override
    public List<Tag> toEntity(List<TagDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Tag> list = new ArrayList<Tag>( dtoList.size() );
        for ( TagDTO tagDTO : dtoList ) {
            list.add( toEntity( tagDTO ) );
        }

        return list;
    }

    @Override
    public List<TagDTO> toDto(List<Tag> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<TagDTO> list = new ArrayList<TagDTO>( entityList.size() );
        for ( Tag tag : entityList ) {
            list.add( toDto( tag ) );
        }

        return list;
    }
}
