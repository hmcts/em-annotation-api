package uk.gov.hmcts.reform.em.annotation.service.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.annotation.domain.Metadata;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-10-31T17:25:26+0000",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 1.3.1100.v20200828-0941, environment: Java 15 (Oracle Corporation)"
)
@Component
public class MetadataMapperImpl implements MetadataMapper {

    @Override
    public Metadata toEntity(MetadataDto dto) {
        if ( dto == null ) {
            return null;
        }

        Metadata metadata = new Metadata();

        return metadata;
    }

    @Override
    public MetadataDto toDto(Metadata entity) {
        if ( entity == null ) {
            return null;
        }

        MetadataDto metadataDto = new MetadataDto();

        return metadataDto;
    }

    @Override
    public List<Metadata> toEntity(List<MetadataDto> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Metadata> list = new ArrayList<Metadata>( dtoList.size() );
        for ( MetadataDto metadataDto : dtoList ) {
            list.add( toEntity( metadataDto ) );
        }

        return list;
    }

    @Override
    public List<MetadataDto> toDto(List<Metadata> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<MetadataDto> list = new ArrayList<MetadataDto>( entityList.size() );
        for ( Metadata metadata : entityList ) {
            list.add( toDto( metadata ) );
        }

        return list;
    }
}
