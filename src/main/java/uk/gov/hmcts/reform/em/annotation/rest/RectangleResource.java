package uk.gov.hmcts.reform.em.annotation.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.rest.util.HeaderUtil;
import uk.gov.hmcts.reform.em.annotation.rest.util.PaginationUtil;
import uk.gov.hmcts.reform.em.annotation.service.RectangleService;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Rectangle.
 */
@RestController
@RequestMapping("/api")
public class RectangleResource {

    private final Logger log = LoggerFactory.getLogger(RectangleResource.class);

    private static final String ENTITY_NAME = "rectangle";

    private final RectangleService rectangleService;

    public RectangleResource(RectangleService rectangleService) {
        this.rectangleService = rectangleService;
    }

    /**
     * POST  /rectangles : Create a new rectangle.
     *
     * @param rectangleDTO the rectangleDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new rectangleDTO, or with status 400 (Bad Request) if the rectangle has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/rectangles")
    //@Timed
    public ResponseEntity<RectangleDTO> createRectangle(@RequestBody RectangleDTO rectangleDTO) throws URISyntaxException {
        log.debug("REST request to save Rectangle : {}", rectangleDTO);
        if (rectangleDTO.getId() != null) {
            throw new BadRequestAlertException("A new rectangle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RectangleDTO result = rectangleService.save(rectangleDTO);
        return ResponseEntity.created(new URI("/api/rectangles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /rectangles : Updates an existing rectangle.
     *
     * @param rectangleDTO the rectangleDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated rectangleDTO,
     * or with status 400 (Bad Request) if the rectangleDTO is not valid,
     * or with status 500 (Internal Server Error) if the rectangleDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/rectangles")
    //@Timed
    public ResponseEntity<RectangleDTO> updateRectangle(@RequestBody RectangleDTO rectangleDTO) throws URISyntaxException {
        log.debug("REST request to update Rectangle : {}", rectangleDTO);
        if (rectangleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RectangleDTO result = rectangleService.save(rectangleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, rectangleDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /rectangles : get all the rectangles.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of rectangles in body
     */
    @GetMapping("/rectangles")
    //@Timed
    public ResponseEntity<List<RectangleDTO>> getAllRectangles(Pageable pageable) {
        log.debug("REST request to get a page of Rectangles");
        Page<RectangleDTO> page = rectangleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rectangles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /rectangles/:id : get the "id" rectangle.
     *
     * @param id the id of the rectangleDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the rectangleDTO, or with status 404 (Not Found)
     */
    @GetMapping("/rectangles/{id}")
    //@Timed
    public ResponseEntity<RectangleDTO> getRectangle(@PathVariable Long id) {
        log.debug("REST request to get Rectangle : {}", id);
        Optional<RectangleDTO> rectangleDTO = rectangleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rectangleDTO);
    }

    /**
     * DELETE  /rectangles/:id : delete the "id" rectangle.
     *
     * @param id the id of the rectangleDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/rectangles/{id}")
    //@Timed
    public ResponseEntity<Void> deleteRectangle(@PathVariable Long id) {
        log.debug("REST request to delete Rectangle : {}", id);
        rectangleService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
