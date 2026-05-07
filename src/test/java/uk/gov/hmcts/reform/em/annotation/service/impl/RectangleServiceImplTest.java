package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.repository.RectangleRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ResourceNotFoundException;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.RectangleMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RectangleServiceImplTest {

    @Mock
    private RectangleRepository rectangleRepository;

    @Mock
    private RectangleMapper rectangleMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private RectangleServiceImpl rectangleService;

    private static final String CURRENT_USER = "testUser";
    private static final String OTHER_USER = "otherUser";

    @Test
    void testSaveSuccess() {
        RectangleDTO rectangleDTO = new RectangleDTO();
        Rectangle rectangle = new Rectangle();
        when(rectangleMapper.toEntity(rectangleDTO)).thenReturn(rectangle);
        when(rectangleRepository.save(rectangle)).thenReturn(rectangle);
        when(rectangleMapper.toDto(rectangle)).thenReturn(rectangleDTO);

        RectangleDTO result = rectangleService.save(rectangleDTO);

        assertNotNull(result);
        verify(rectangleRepository).save(rectangle);
    }

    @Test
    void testFindAllReturnsOnlyCurrentUserRectangles() {
        Pageable pageable = Pageable.unpaged();
        Rectangle rectangle = new Rectangle();
        Page<Rectangle> page = new PageImpl<>(List.of(rectangle));
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(rectangleRepository.findByCreatedBy(CURRENT_USER, pageable)).thenReturn(page);
        when(rectangleMapper.toDto(rectangle)).thenReturn(new RectangleDTO());

        Page<RectangleDTO> result = rectangleService.findAll(pageable);

        assertNotNull(result);
        verify(rectangleRepository).findByCreatedBy(CURRENT_USER, pageable);
    }

    @Test
    void testFindOneReturnsRectangleWhenOwner() {
        UUID id = UUID.randomUUID();
        Rectangle rectangle = new Rectangle();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(rectangleRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.of(rectangle));
        when(rectangleMapper.toDto(rectangle)).thenReturn(new RectangleDTO());

        Optional<RectangleDTO> result = rectangleService.findOne(id);

        assertTrue(result.isPresent());
        verify(rectangleRepository).findByIdAndCreatedBy(id, CURRENT_USER);
    }

    @Test
    void testFindOneReturnsEmptyWhenNotOwner() {
        UUID id = UUID.randomUUID();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(rectangleRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.empty());

        Optional<RectangleDTO> result = rectangleService.findOne(id);

        assertFalse(result.isPresent());
        verify(rectangleRepository).findByIdAndCreatedBy(id, CURRENT_USER);
    }

    @Test
    void testFindOneReturnsEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(rectangleRepository.findByIdAndCreatedBy(id, CURRENT_USER)).thenReturn(Optional.empty());

        Optional<RectangleDTO> result = rectangleService.findOne(id);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteSuccess() {
        UUID id = UUID.randomUUID();
        Rectangle rectangle = new Rectangle();
        rectangle.setCreatedBy(CURRENT_USER);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(rectangleRepository.findById(id)).thenReturn(Optional.of(rectangle));

        rectangleService.delete(id);

        verify(rectangleRepository).deleteById(id);
    }

    @Test
    void testDeleteThrowsWhenNotOwner() {
        UUID id = UUID.randomUUID();
        Rectangle rectangle = new Rectangle();
        rectangle.setCreatedBy(OTHER_USER);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(rectangleRepository.findById(id)).thenReturn(Optional.of(rectangle));

        assertThrows(ResourceNotFoundException.class, () -> rectangleService.delete(id));
        verify(rectangleRepository, never()).deleteById(any());
    }

    @Test
    void testGetCurrentUserThrowsWhenNotAuthenticated() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> rectangleService.findAll(Pageable.unpaged()));
    }
}
