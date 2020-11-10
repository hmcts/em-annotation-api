package uk.gov.hmcts.reform.em.annotation.service.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-10-31T17:25:26+0000",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 1.3.1100.v20200828-0941, environment: Java 15 (Oracle Corporation)"
)
@Component
public class AnnotationSetMapperImpl implements AnnotationSetMapper {

    @Autowired
    private AnnotationMapper annotationMapper;

    @Override
    public AnnotationSetDTO toDto(AnnotationSet entity) {
        if ( entity == null ) {
            return null;
        }

        AnnotationSetDTO annotationSetDTO = new AnnotationSetDTO();

        annotationSetDTO.setCreatedBy( entity.getCreatedBy() );
        annotationSetDTO.setCreatedDate( entity.getCreatedDate() );
        annotationSetDTO.setLastModifiedBy( entity.getLastModifiedBy() );
        annotationSetDTO.setLastModifiedDate( entity.getLastModifiedDate() );
        annotationSetDTO.setCreatedByDetails( entity.getCreatedByDetails() );
        annotationSetDTO.setLastModifiedByDetails( entity.getLastModifiedByDetails() );
        annotationSetDTO.setAnnotations( annotationSetToAnnotationDTOSet( entity.getAnnotations() ) );
        annotationSetDTO.setId( entity.getId() );
        annotationSetDTO.setDocumentId( entity.getDocumentId() );

        return annotationSetDTO;
    }

    @Override
    public List<AnnotationSet> toEntity(List<AnnotationSetDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<AnnotationSet> list = new ArrayList<AnnotationSet>( dtoList.size() );
        for ( AnnotationSetDTO annotationSetDTO : dtoList ) {
            list.add( toEntity( annotationSetDTO ) );
        }

        return list;
    }

    @Override
    public List<AnnotationSetDTO> toDto(List<AnnotationSet> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<AnnotationSetDTO> list = new ArrayList<AnnotationSetDTO>( entityList.size() );
        for ( AnnotationSet annotationSet : entityList ) {
            list.add( toDto( annotationSet ) );
        }

        return list;
    }

    @Override
    public AnnotationSet toEntity(AnnotationSetDTO annotationSetDTO) {
        if ( annotationSetDTO == null ) {
            return null;
        }

        AnnotationSet annotationSet = new AnnotationSet();

        annotationSet.setCreatedBy( annotationSetDTO.getCreatedBy() );
        annotationSet.setCreatedDate( annotationSetDTO.getCreatedDate() );
        annotationSet.setLastModifiedBy( annotationSetDTO.getLastModifiedBy() );
        annotationSet.setLastModifiedDate( annotationSetDTO.getLastModifiedDate() );
        annotationSet.setId( annotationSetDTO.getId() );
        annotationSet.setDocumentId( annotationSetDTO.getDocumentId() );

        return annotationSet;
    }

    protected Set<AnnotationDTO> annotationSetToAnnotationDTOSet(Set<Annotation> set) {
        if ( set == null ) {
            return null;
        }

        Set<AnnotationDTO> set1 = new HashSet<AnnotationDTO>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Annotation annotation : set ) {
            set1.add( annotationMapper.toDto( annotation ) );
        }

        return set1;
    }
}
