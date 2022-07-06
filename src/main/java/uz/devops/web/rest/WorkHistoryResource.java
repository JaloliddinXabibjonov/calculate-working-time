package uz.devops.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import uz.devops.repository.WorkHistoryRepository;
import uz.devops.service.WorkHistoryQueryService;
import uz.devops.service.WorkHistoryService;
import uz.devops.service.criteria.WorkHistoryCriteria;
import uz.devops.service.dto.WorkHistoryDTO;
import uz.devops.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.domain.WorkHistory}.
 */
@RestController
@RequestMapping("/api")
public class WorkHistoryResource {

    private final Logger log = LoggerFactory.getLogger(WorkHistoryResource.class);

    private static final String ENTITY_NAME = "workHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkHistoryService workHistoryService;

    private final WorkHistoryRepository workHistoryRepository;

    private final WorkHistoryQueryService workHistoryQueryService;

    public WorkHistoryResource(
        WorkHistoryService workHistoryService,
        WorkHistoryRepository workHistoryRepository,
        WorkHistoryQueryService workHistoryQueryService
    ) {
        this.workHistoryService = workHistoryService;
        this.workHistoryRepository = workHistoryRepository;
        this.workHistoryQueryService = workHistoryQueryService;
    }

    /**
     * {@code POST  /work-histories} : Create a new workHistory.
     *
     * @param workHistoryDTO the workHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workHistoryDTO, or with status {@code 400 (Bad Request)} if the workHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/work-histories")
    public ResponseEntity<WorkHistoryDTO> createWorkHistory(@RequestBody WorkHistoryDTO workHistoryDTO) throws URISyntaxException {
        log.debug("REST request to save WorkHistory : {}", workHistoryDTO);
        if (workHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new workHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WorkHistoryDTO result = workHistoryService.save(workHistoryDTO);
        return ResponseEntity
            .created(new URI("/api/work-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /work-histories/:id} : Updates an existing workHistory.
     *
     * @param id the id of the workHistoryDTO to save.
     * @param workHistoryDTO the workHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the workHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/work-histories/{id}")
    public ResponseEntity<WorkHistoryDTO> updateWorkHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody WorkHistoryDTO workHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update WorkHistory : {}, {}", id, workHistoryDTO);
        if (workHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WorkHistoryDTO result = workHistoryService.update(workHistoryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workHistoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /work-histories/:id} : Partial updates given fields of an existing workHistory, field will ignore if it is null
     *
     * @param id the id of the workHistoryDTO to save.
     * @param workHistoryDTO the workHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the workHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/work-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkHistoryDTO> partialUpdateWorkHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody WorkHistoryDTO workHistoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update WorkHistory partially : {}, {}", id, workHistoryDTO);
        if (workHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkHistoryDTO> result = workHistoryService.partialUpdate(workHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /work-histories} : get all the workHistories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workHistories in body.
     */
    @GetMapping("/work-histories")
    public ResponseEntity<List<WorkHistoryDTO>> getAllWorkHistories(
        WorkHistoryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get WorkHistories by criteria: {}", criteria);
        Page<WorkHistoryDTO> page = workHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /work-histories/count} : count all the workHistories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/work-histories/count")
    public ResponseEntity<Long> countWorkHistories(WorkHistoryCriteria criteria) {
        log.debug("REST request to count WorkHistories by criteria: {}", criteria);
        return ResponseEntity.ok().body(workHistoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /work-histories/:id} : get the "id" workHistory.
     *
     * @param id the id of the workHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/work-histories/{id}")
    public ResponseEntity<WorkHistoryDTO> getWorkHistory(@PathVariable Long id) {
        log.debug("REST request to get WorkHistory : {}", id);
        Optional<WorkHistoryDTO> workHistoryDTO = workHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workHistoryDTO);
    }

    /**
     * {@code DELETE  /work-histories/:id} : delete the "id" workHistory.
     *
     * @param id the id of the workHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/work-histories/{id}")
    public ResponseEntity<Void> deleteWorkHistory(@PathVariable Long id) {
        log.debug("REST request to delete WorkHistory : {}", id);
        workHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
