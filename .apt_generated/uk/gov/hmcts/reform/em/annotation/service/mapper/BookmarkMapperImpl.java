package uk.gov.hmcts.reform.em.annotation.service.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-10-31T17:25:19+0000",
    comments = "version: 1.2.0.Final, compiler: Eclipse JDT (IDE) 1.3.1100.v20200828-0941, environment: Java 15 (Oracle Corporation)"
)
@Component
public class BookmarkMapperImpl implements BookmarkMapper {

    @Override
    public Bookmark toEntity(BookmarkDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Bookmark bookmark = new Bookmark();

        bookmark.setId( dto.getId() );
        bookmark.setName( dto.getName() );
        bookmark.setDocumentId( dto.getDocumentId() );
        bookmark.setCreatedBy( dto.getCreatedBy() );
        bookmark.setPageNumber( dto.getPageNumber() );
        bookmark.setxCoordinate( dto.getxCoordinate() );
        bookmark.setyCoordinate( dto.getyCoordinate() );
        bookmark.setParent( dto.getParent() );
        bookmark.setPrevious( dto.getPrevious() );

        return bookmark;
    }

    @Override
    public BookmarkDTO toDto(Bookmark entity) {
        if ( entity == null ) {
            return null;
        }

        BookmarkDTO bookmarkDTO = new BookmarkDTO();

        bookmarkDTO.setId( entity.getId() );
        bookmarkDTO.setName( entity.getName() );
        bookmarkDTO.setDocumentId( entity.getDocumentId() );
        bookmarkDTO.setCreatedBy( entity.getCreatedBy() );
        bookmarkDTO.setPageNumber( entity.getPageNumber() );
        bookmarkDTO.setxCoordinate( entity.getxCoordinate() );
        bookmarkDTO.setyCoordinate( entity.getyCoordinate() );
        bookmarkDTO.setParent( entity.getParent() );
        bookmarkDTO.setPrevious( entity.getPrevious() );

        return bookmarkDTO;
    }

    @Override
    public List<Bookmark> toEntity(List<BookmarkDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Bookmark> list = new ArrayList<Bookmark>( dtoList.size() );
        for ( BookmarkDTO bookmarkDTO : dtoList ) {
            list.add( toEntity( bookmarkDTO ) );
        }

        return list;
    }

    @Override
    public List<BookmarkDTO> toDto(List<Bookmark> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<BookmarkDTO> list = new ArrayList<BookmarkDTO>( entityList.size() );
        for ( Bookmark bookmark : entityList ) {
            list.add( toDto( bookmark ) );
        }

        return list;
    }
}
