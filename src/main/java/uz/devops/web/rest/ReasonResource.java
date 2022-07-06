package uz.devops.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.devops.repository.ReasonRepository;
import uz.devops.service.ReasonQueryService;
import uz.devops.service.ReasonService;
import uz.devops.service.criteria.ReasonCriteria;
import uz.devops.service.dto.ReasonDTO;
import uz.devops.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.domain.Reason}.
 */
@RestController
@RequestMapping("/api")
public class ReasonResource {

    private final Logger log = LoggerFactory.getLogger(ReasonResource.class);

    private static final String ENTITY_NAME = "reason";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReasonService reasonService;

    private final ReasonRepository reasonRepository;

    private final ReasonQueryService reasonQueryService;

    public ReasonResource(ReasonService reasonService, ReasonRepository reasonRepository, ReasonQueryService reasonQueryService) {
        this.reasonService = reasonService;
        this.reasonRepository = reasonRepository;
        this.reasonQueryService = reasonQueryService;
    }

    /**
     * {@code POST  /reasons} : Create a new reason.
     *
     * @param reasonDTO the reasonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reasonDTO, or with status {@code 400 (Bad Request)} if the reason has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reasons")
    public ResponseEntity<ReasonDTO> createReason(@Valid @RequestBody ReasonDTO reasonDTO) throws URISyntaxException {
        log.debug("REST request to save Reason : {}", reasonDTO);
        if (reasonDTO.getId() != null) {
            throw new BadRequestAlertException("A new reason cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ReasonDTO result = reasonService.save(reasonDTO);
        return ResponseEntity
            .created(new URI("/api/reasons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reasons/:id} : Updates an existing reason.
     *
     * @param id the id of the reasonDTO to save.
     * @param reasonDTO the reasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reasonDTO,
     * or with status {@code 400 (Bad Request)} if the reasonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reasons/{id}")
    public ResponseEntity<ReasonDTO> updateReason(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReasonDTO reasonDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Reason : {}, {}", id, reasonDTO);
        if (reasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ReasonDTO result = reasonService.update(reasonDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, reasonDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /reasons/:id} : Partial updates given fields of an existing reason, field will ignore if it is null
     *
     * @param id the id of the reasonDTO to save.
     * @param reasonDTO the reasonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reasonDTO,
     * or with status {@code 400 (Bad Request)} if the reasonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the reasonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the reasonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reasons/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReasonDTO> partialUpdateReason(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReasonDTO reasonDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reason partially : {}, {}", id, reasonDTO);
        if (reasonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reasonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reasonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReasonDTO> result = reasonService.partialUpdate(reasonDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, reasonDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /reasons} : get all the reasons.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reasons in body.
     */
    @GetMapping("/reasons")
    public ResponseEntity<List<ReasonDTO>> getAllReasons(
        ReasonCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Reasons by criteria: {}", criteria);
        Page<ReasonDTO> page = reasonQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reasons/count} : count all the reasons.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/reasons/count")
    public ResponseEntity<Long> countReasons(ReasonCriteria criteria) {
        log.debug("REST request to count Reasons by criteria: {}", criteria);
        return ResponseEntity.ok().body(reasonQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /reasons/:id} : get the "id" reason.
     *
     * @param id the id of the reasonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reasonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reasons/{id}")
    public ResponseEntity<ReasonDTO> getReason(@PathVariable Long id) {
        log.debug("REST request to get Reason : {}", id);
        Optional<ReasonDTO> reasonDTO = reasonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reasonDTO);
    }

    /**
     * {@code DELETE  /reasons/:id} : delete the "id" reason.
     *
     * @param id the id of the reasonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reasons/{id}")
    public ResponseEntity<Void> deleteReason(@PathVariable Long id) {
        log.debug("REST request to delete Reason : {}", id);
        reasonService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
