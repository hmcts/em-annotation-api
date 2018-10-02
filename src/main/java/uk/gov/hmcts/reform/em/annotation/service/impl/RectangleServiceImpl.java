package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.repository.RectangleRepository;
import uk.gov.hmcts.reform.em.annotation.service.RectangleService;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.RectangleMapper;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing Rectangle.
 */
@Service
@Transactional
public class RectangleServiceImpl implements RectangleService {

    private final Logger log = LoggerFactory.getLogger(RectangleServiceImpl.class);

    private final RectangleRepository rectangleRepository;

    private final RectangleMapper rectangleMapper;

    public RectangleServiceImpl(RectangleRepository rectangleRepository, RectangleMapper rectangleMapper) {
        this.rectangleRepository = rectangleRepository;
        this.rectangleMapper = rectangleMapper;
    }

    /**
     * Save a rectangle.
     *
     * @param rectangleDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public RectangleDTO save(RectangleDTO rectangleDTO) {
        log.debug("Request to save Rectangle : {}", rectangleDTO);
        Rectangle rectangle = rectangleMapper.toEntity(rectangleDTO);
        rectangle = rectangleRepository.save(rectangle);
        return rectangleMapper.toDto(rectangle);
    }

    /**
     * Get all the rectangles.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RectangleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Rectangles");
        return rectangleRepository.findAll(pageable)
            .map(rectangleMapper::toDto);
    }


    /**
     * Get one rectangle by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RectangleDTO> findOne(UUID id) {
        log.debug("Request to get Rectangle : {}", id);
        return rectangleRepository.findById(id)
            .map(rectangleMapper::toDto);
    }

    /**
     * Delete the rectangle by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Rectangle : {}", id);
        rectangleRepository.deleteById(id);
    }
}
