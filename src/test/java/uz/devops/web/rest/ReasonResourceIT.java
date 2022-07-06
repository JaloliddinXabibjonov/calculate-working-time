package uz.devops.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.IntegrationTest;
import uz.devops.domain.Action;
import uz.devops.domain.Reason;
import uz.devops.domain.Reason;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.ReasonRepository;
import uz.devops.service.criteria.ReasonCriteria;
import uz.devops.service.dto.ReasonDTO;
import uz.devops.service.mapper.ReasonMapper;

/**
 * Integration tests for the {@link ReasonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReasonResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.WITH_REASON;
    private static final Status UPDATED_STATUS = Status.AT_WORK;

    private static final String ENTITY_API_URL = "/api/reasons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReasonRepository reasonRepository;

    @Autowired
    private ReasonMapper reasonMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReasonMockMvc;

    private Reason reason;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reason createEntity(EntityManager em) {
        Reason reason = new Reason().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).status(DEFAULT_STATUS);
        return reason;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reason createUpdatedEntity(EntityManager em) {
        Reason reason = new Reason().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);
        return reason;
    }

    @BeforeEach
    public void initTest() {
        reason = createEntity(em);
    }

    @Test
    @Transactional
    void createReason() throws Exception {
        int databaseSizeBeforeCreate = reasonRepository.findAll().size();
        // Create the Reason
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);
        restReasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reasonDTO)))
            .andExpect(status().isCreated());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeCreate + 1);
        Reason testReason = reasonList.get(reasonList.size() - 1);
        assertThat(testReason.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testReason.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testReason.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createReasonWithExistingId() throws Exception {
        // Create the Reason with an existing ID
        reason.setId(1L);
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);

        int databaseSizeBeforeCreate = reasonRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reasonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = reasonRepository.findAll().size();
        // set the field null
        reason.setName(null);

        // Create the Reason, which fails.
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);

        restReasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reasonDTO)))
            .andExpect(status().isBadRequest());

        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = reasonRepository.findAll().size();
        // set the field null
        reason.setStatus(null);

        // Create the Reason, which fails.
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);

        restReasonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reasonDTO)))
            .andExpect(status().isBadRequest());

        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReasons() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList
        restReasonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reason.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getReason() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get the reason
        restReasonMockMvc
            .perform(get(ENTITY_API_URL_ID, reason.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reason.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getReasonsByIdFiltering() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        Long id = reason.getId();

        defaultReasonShouldBeFound("id.equals=" + id);
        defaultReasonShouldNotBeFound("id.notEquals=" + id);

        defaultReasonShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultReasonShouldNotBeFound("id.greaterThan=" + id);

        defaultReasonShouldBeFound("id.lessThanOrEqual=" + id);
        defaultReasonShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReasonsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where name equals to DEFAULT_NAME
        defaultReasonShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the reasonList where name equals to UPDATED_NAME
        defaultReasonShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllReasonsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where name not equals to DEFAULT_NAME
        defaultReasonShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the reasonList where name not equals to UPDATED_NAME
        defaultReasonShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllReasonsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where name in DEFAULT_NAME or UPDATED_NAME
        defaultReasonShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the reasonList where name equals to UPDATED_NAME
        defaultReasonShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllReasonsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where name is not null
        defaultReasonShouldBeFound("name.specified=true");

        // Get all the reasonList where name is null
        defaultReasonShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllReasonsByNameContainsSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where name contains DEFAULT_NAME
        defaultReasonShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the reasonList where name contains UPDATED_NAME
        defaultReasonShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllReasonsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where name does not contain DEFAULT_NAME
        defaultReasonShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the reasonList where name does not contain UPDATED_NAME
        defaultReasonShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllReasonsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where description equals to DEFAULT_DESCRIPTION
        defaultReasonShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the reasonList where description equals to UPDATED_DESCRIPTION
        defaultReasonShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReasonsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where description not equals to DEFAULT_DESCRIPTION
        defaultReasonShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the reasonList where description not equals to UPDATED_DESCRIPTION
        defaultReasonShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReasonsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultReasonShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the reasonList where description equals to UPDATED_DESCRIPTION
        defaultReasonShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReasonsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where description is not null
        defaultReasonShouldBeFound("description.specified=true");

        // Get all the reasonList where description is null
        defaultReasonShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllReasonsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where description contains DEFAULT_DESCRIPTION
        defaultReasonShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the reasonList where description contains UPDATED_DESCRIPTION
        defaultReasonShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReasonsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where description does not contain DEFAULT_DESCRIPTION
        defaultReasonShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the reasonList where description does not contain UPDATED_DESCRIPTION
        defaultReasonShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllReasonsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where status equals to DEFAULT_STATUS
        defaultReasonShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the reasonList where status equals to UPDATED_STATUS
        defaultReasonShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReasonsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where status not equals to DEFAULT_STATUS
        defaultReasonShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the reasonList where status not equals to UPDATED_STATUS
        defaultReasonShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReasonsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultReasonShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the reasonList where status equals to UPDATED_STATUS
        defaultReasonShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReasonsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        // Get all the reasonList where status is not null
        defaultReasonShouldBeFound("status.specified=true");

        // Get all the reasonList where status is null
        defaultReasonShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllReasonsByParentIsEqualToSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);
        Reason parent;
        if (TestUtil.findAll(em, Reason.class).isEmpty()) {
            parent = ReasonResourceIT.createEntity(em);
            em.persist(parent);
            em.flush();
        } else {
            parent = TestUtil.findAll(em, Reason.class).get(0);
        }
        em.persist(parent);
        em.flush();
        reason.setParent(parent);
        reasonRepository.saveAndFlush(reason);
        Long parentId = parent.getId();

        // Get all the reasonList where parent equals to parentId
        defaultReasonShouldBeFound("parentId.equals=" + parentId);

        // Get all the reasonList where parent equals to (parentId + 1)
        defaultReasonShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    @Test
    @Transactional
    void getAllReasonsByActionIsEqualToSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);
        Action action;
        if (TestUtil.findAll(em, Action.class).isEmpty()) {
            action = ActionResourceIT.createEntity(em);
            em.persist(action);
            em.flush();
        } else {
            action = TestUtil.findAll(em, Action.class).get(0);
        }
        em.persist(action);
        em.flush();
        reason.setAction(action);
        action.setReason(reason);
        reasonRepository.saveAndFlush(reason);
        Long actionId = action.getId();

        // Get all the reasonList where action equals to actionId
        defaultReasonShouldBeFound("actionId.equals=" + actionId);

        // Get all the reasonList where action equals to (actionId + 1)
        defaultReasonShouldNotBeFound("actionId.equals=" + (actionId + 1));
    }

    @Test
    @Transactional
    void getAllReasonsByWorkHistoryIsEqualToSomething() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);
        WorkHistory workHistory;
        if (TestUtil.findAll(em, WorkHistory.class).isEmpty()) {
            workHistory = WorkHistoryResourceIT.createEntity(em);
            em.persist(workHistory);
            em.flush();
        } else {
            workHistory = TestUtil.findAll(em, WorkHistory.class).get(0);
        }
        em.persist(workHistory);
        em.flush();
        reason.addWorkHistory(workHistory);
        reasonRepository.saveAndFlush(reason);
        Long workHistoryId = workHistory.getId();

        // Get all the reasonList where workHistory equals to workHistoryId
        defaultReasonShouldBeFound("workHistoryId.equals=" + workHistoryId);

        // Get all the reasonList where workHistory equals to (workHistoryId + 1)
        defaultReasonShouldNotBeFound("workHistoryId.equals=" + (workHistoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReasonShouldBeFound(String filter) throws Exception {
        restReasonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reason.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restReasonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReasonShouldNotBeFound(String filter) throws Exception {
        restReasonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReasonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReason() throws Exception {
        // Get the reason
        restReasonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewReason() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        int databaseSizeBeforeUpdate = reasonRepository.findAll().size();

        // Update the reason
        Reason updatedReason = reasonRepository.findById(reason.getId()).get();
        // Disconnect from session so that the updates on updatedReason are not directly saved in db
        em.detach(updatedReason);
        updatedReason.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);
        ReasonDTO reasonDTO = reasonMapper.toDto(updatedReason);

        restReasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reasonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reasonDTO))
            )
            .andExpect(status().isOk());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeUpdate);
        Reason testReason = reasonList.get(reasonList.size() - 1);
        assertThat(testReason.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testReason.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testReason.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingReason() throws Exception {
        int databaseSizeBeforeUpdate = reasonRepository.findAll().size();
        reason.setId(count.incrementAndGet());

        // Create the Reason
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reasonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReason() throws Exception {
        int databaseSizeBeforeUpdate = reasonRepository.findAll().size();
        reason.setId(count.incrementAndGet());

        // Create the Reason
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReasonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReason() throws Exception {
        int databaseSizeBeforeUpdate = reasonRepository.findAll().size();
        reason.setId(count.incrementAndGet());

        // Create the Reason
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReasonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reasonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReasonWithPatch() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        int databaseSizeBeforeUpdate = reasonRepository.findAll().size();

        // Update the reason using partial update
        Reason partialUpdatedReason = new Reason();
        partialUpdatedReason.setId(reason.getId());

        partialUpdatedReason.status(UPDATED_STATUS);

        restReasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReason.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReason))
            )
            .andExpect(status().isOk());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeUpdate);
        Reason testReason = reasonList.get(reasonList.size() - 1);
        assertThat(testReason.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testReason.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testReason.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateReasonWithPatch() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        int databaseSizeBeforeUpdate = reasonRepository.findAll().size();

        // Update the reason using partial update
        Reason partialUpdatedReason = new Reason();
        partialUpdatedReason.setId(reason.getId());

        partialUpdatedReason.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).status(UPDATED_STATUS);

        restReasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReason.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReason))
            )
            .andExpect(status().isOk());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeUpdate);
        Reason testReason = reasonList.get(reasonList.size() - 1);
        assertThat(testReason.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testReason.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testReason.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingReason() throws Exception {
        int databaseSizeBeforeUpdate = reasonRepository.findAll().size();
        reason.setId(count.incrementAndGet());

        // Create the Reason
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reasonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReason() throws Exception {
        int databaseSizeBeforeUpdate = reasonRepository.findAll().size();
        reason.setId(count.incrementAndGet());

        // Create the Reason
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReasonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reasonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReason() throws Exception {
        int databaseSizeBeforeUpdate = reasonRepository.findAll().size();
        reason.setId(count.incrementAndGet());

        // Create the Reason
        ReasonDTO reasonDTO = reasonMapper.toDto(reason);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReasonMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(reasonDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reason in the database
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReason() throws Exception {
        // Initialize the database
        reasonRepository.saveAndFlush(reason);

        int databaseSizeBeforeDelete = reasonRepository.findAll().size();

        // Delete the reason
        restReasonMockMvc
            .perform(delete(ENTITY_API_URL_ID, reason.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reason> reasonList = reasonRepository.findAll();
        assertThat(reasonList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
