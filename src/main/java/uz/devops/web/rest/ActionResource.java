package uz.devops.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import uz.devops.repository.ActionRepository;
import uz.devops.service.ActionQueryService;
import uz.devops.service.ActionService;
import uz.devops.service.criteria.ActionCriteria;
import uz.devops.service.dto.ActionDTO;
import uz.devops.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.domain.Action}.
 */
@RestController
@RequestMapping("/api")
public class ActionResource {

    private final Logger log = LoggerFactory.getLogger(ActionResource.class);

    private static final String ENTITY_NAME = "action";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActionService actionService;

    private final ActionRepository actionRepository;

    private final ActionQueryService actionQueryService;

    public ActionResource(ActionService actionService, ActionRepository actionRepository, ActionQueryService actionQueryService) {
        this.actionService = actionService;
        this.actionRepository = actionRepository;
        this.actionQueryService = actionQueryService;
    }

    /**
     * {@code POST  /actions} : Create a new action.
     *
     * @param actionDTO the actionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new actionDTO, or with status {@code 400 (Bad Request)} if the action has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/actions")
    public ResponseEntity<ActionDTO> createAction(@Valid @RequestBody ActionDTO actionDTO) throws URISyntaxException {
        log.debug("REST request to save Action : {}", actionDTO);
        if (actionDTO.getId() != null) {
            throw new BadRequestAlertException("A new action cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ActionDTO result = actionService.save(actionDTO);
        return ResponseEntity
            .created(new URI("/api/actions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /actions/:id} : Updates an existing action.
     *
     * @param id the id of the actionDTO to save.
     * @param actionDTO the actionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionDTO,
     * or with status {@code 400 (Bad Request)} if the actionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the actionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/actions/{id}")
    public ResponseEntity<ActionDTO> updateAction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ActionDTO actionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Action : {}, {}", id, actionDTO);
        if (actionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ActionDTO result = actionService.update(actionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /actions/:id} : Partial updates given fields of an existing action, field will ignore if it is null
     *
     * @param id the id of the actionDTO to save.
     * @param actionDTO the actionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionDTO,
     * or with status {@code 400 (Bad Request)} if the actionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the actionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the actionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/actions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ActionDTO> partialUpdateAction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ActionDTO actionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Action partially : {}, {}", id, actionDTO);
        if (actionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ActionDTO> result = actionService.partialUpdate(actionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, actionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /actions} : get all the actions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actions in body.
     */
    @GetMapping("/actions")
    public ResponseEntity<List<ActionDTO>> getAllActions(
        ActionCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Actions by criteria: {}", criteria);
        Page<ActionDTO> page = actionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /actions/count} : count all the actions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/actions/count")
    public ResponseEntity<Long> countActions(ActionCriteria criteria) {
        log.debug("REST request to count Actions by criteria: {}", criteria);
        return ResponseEntity.ok().body(actionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /actions/:id} : get the "id" action.
     *
     * @param id the id of the actionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/actions/{id}")
    public ResponseEntity<ActionDTO> getAction(@PathVariable Long id) {
        log.debug("REST request to get Action : {}", id);
        Optional<ActionDTO> actionDTO = actionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(actionDTO);
    }

    /**
     * {@code DELETE  /actions/:id} : delete the "id" action.
     *
     * @param id the id of the actionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/actions/{id}")
    public ResponseEntity<Void> deleteAction(@PathVariable Long id) {
        log.debug("REST request to delete Action : {}", id);
        actionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
