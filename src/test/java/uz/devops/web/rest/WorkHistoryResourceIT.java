package uz.devops.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import uz.devops.domain.Reason;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.Worker;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.WorkHistoryRepository;
import uz.devops.service.criteria.WorkHistoryCriteria;
import uz.devops.service.dto.WorkHistoryDTO;
import uz.devops.service.mapper.WorkHistoryMapper;

/**
 * Integration tests for the {@link WorkHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WorkHistoryResourceIT {

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_TO_LUNCH = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TO_LUNCH = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FROM_LUNCH = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FROM_LUNCH = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REASON_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_REASON_DESCRIPTION = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.WITH_REASON;
    private static final Status UPDATED_STATUS = Status.AT_WORK;

    private static final String ENTITY_API_URL = "/api/work-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkHistoryRepository workHistoryRepository;

    @Autowired
    private WorkHistoryMapper workHistoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkHistoryMockMvc;

    private WorkHistory workHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkHistory createEntity(EntityManager em) {
        WorkHistory workHistory = new WorkHistory()
            .start(DEFAULT_START)
            .toLunch(DEFAULT_TO_LUNCH)
            .fromLunch(DEFAULT_FROM_LUNCH)
            .end(DEFAULT_END)
            .reasonDescription(DEFAULT_REASON_DESCRIPTION)
            .status(DEFAULT_STATUS);
        return workHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkHistory createUpdatedEntity(EntityManager em) {
        WorkHistory workHistory = new WorkHistory()
            .start(UPDATED_START)
            .toLunch(UPDATED_TO_LUNCH)
            .fromLunch(UPDATED_FROM_LUNCH)
            .end(UPDATED_END)
            .reasonDescription(UPDATED_REASON_DESCRIPTION)
            .status(UPDATED_STATUS);
        return workHistory;
    }

    @BeforeEach
    public void initTest() {
        workHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createWorkHistory() throws Exception {
        int databaseSizeBeforeCreate = workHistoryRepository.findAll().size();
        // Create the WorkHistory
        WorkHistoryDTO workHistoryDTO = workHistoryMapper.toDto(workHistory);
        restWorkHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workHistoryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        WorkHistory testWorkHistory = workHistoryList.get(workHistoryList.size() - 1);
        assertThat(testWorkHistory.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testWorkHistory.getToLunch()).isEqualTo(DEFAULT_TO_LUNCH);
        assertThat(testWorkHistory.getFromLunch()).isEqualTo(DEFAULT_FROM_LUNCH);
        assertThat(testWorkHistory.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testWorkHistory.getReasonDescription()).isEqualTo(DEFAULT_REASON_DESCRIPTION);
        assertThat(testWorkHistory.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createWorkHistoryWithExistingId() throws Exception {
        // Create the WorkHistory with an existing ID
        workHistory.setId(1L);
        WorkHistoryDTO workHistoryDTO = workHistoryMapper.toDto(workHistory);

        int databaseSizeBeforeCreate = workHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWorkHistories() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList
        restWorkHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].toLunch").value(hasItem(DEFAULT_TO_LUNCH.toString())))
            .andExpect(jsonPath("$.[*].fromLunch").value(hasItem(DEFAULT_FROM_LUNCH.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())))
            .andExpect(jsonPath("$.[*].reasonDescription").value(hasItem(DEFAULT_REASON_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getWorkHistory() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get the workHistory
        restWorkHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, workHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workHistory.getId().intValue()))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.toLunch").value(DEFAULT_TO_LUNCH.toString()))
            .andExpect(jsonPath("$.fromLunch").value(DEFAULT_FROM_LUNCH.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()))
            .andExpect(jsonPath("$.reasonDescription").value(DEFAULT_REASON_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getWorkHistoriesByIdFiltering() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        Long id = workHistory.getId();

        defaultWorkHistoryShouldBeFound("id.equals=" + id);
        defaultWorkHistoryShouldNotBeFound("id.notEquals=" + id);

        defaultWorkHistoryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWorkHistoryShouldNotBeFound("id.greaterThan=" + id);

        defaultWorkHistoryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWorkHistoryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByStartIsEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where start equals to DEFAULT_START
        defaultWorkHistoryShouldBeFound("start.equals=" + DEFAULT_START);

        // Get all the workHistoryList where start equals to UPDATED_START
        defaultWorkHistoryShouldNotBeFound("start.equals=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByStartIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where start not equals to DEFAULT_START
        defaultWorkHistoryShouldNotBeFound("start.notEquals=" + DEFAULT_START);

        // Get all the workHistoryList where start not equals to UPDATED_START
        defaultWorkHistoryShouldBeFound("start.notEquals=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByStartIsInShouldWork() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where start in DEFAULT_START or UPDATED_START
        defaultWorkHistoryShouldBeFound("start.in=" + DEFAULT_START + "," + UPDATED_START);

        // Get all the workHistoryList where start equals to UPDATED_START
        defaultWorkHistoryShouldNotBeFound("start.in=" + UPDATED_START);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where start is not null
        defaultWorkHistoryShouldBeFound("start.specified=true");

        // Get all the workHistoryList where start is null
        defaultWorkHistoryShouldNotBeFound("start.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByToLunchIsEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where toLunch equals to DEFAULT_TO_LUNCH
        defaultWorkHistoryShouldBeFound("toLunch.equals=" + DEFAULT_TO_LUNCH);

        // Get all the workHistoryList where toLunch equals to UPDATED_TO_LUNCH
        defaultWorkHistoryShouldNotBeFound("toLunch.equals=" + UPDATED_TO_LUNCH);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByToLunchIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where toLunch not equals to DEFAULT_TO_LUNCH
        defaultWorkHistoryShouldNotBeFound("toLunch.notEquals=" + DEFAULT_TO_LUNCH);

        // Get all the workHistoryList where toLunch not equals to UPDATED_TO_LUNCH
        defaultWorkHistoryShouldBeFound("toLunch.notEquals=" + UPDATED_TO_LUNCH);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByToLunchIsInShouldWork() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where toLunch in DEFAULT_TO_LUNCH or UPDATED_TO_LUNCH
        defaultWorkHistoryShouldBeFound("toLunch.in=" + DEFAULT_TO_LUNCH + "," + UPDATED_TO_LUNCH);

        // Get all the workHistoryList where toLunch equals to UPDATED_TO_LUNCH
        defaultWorkHistoryShouldNotBeFound("toLunch.in=" + UPDATED_TO_LUNCH);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByToLunchIsNullOrNotNull() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where toLunch is not null
        defaultWorkHistoryShouldBeFound("toLunch.specified=true");

        // Get all the workHistoryList where toLunch is null
        defaultWorkHistoryShouldNotBeFound("toLunch.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByFromLunchIsEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where fromLunch equals to DEFAULT_FROM_LUNCH
        defaultWorkHistoryShouldBeFound("fromLunch.equals=" + DEFAULT_FROM_LUNCH);

        // Get all the workHistoryList where fromLunch equals to UPDATED_FROM_LUNCH
        defaultWorkHistoryShouldNotBeFound("fromLunch.equals=" + UPDATED_FROM_LUNCH);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByFromLunchIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where fromLunch not equals to DEFAULT_FROM_LUNCH
        defaultWorkHistoryShouldNotBeFound("fromLunch.notEquals=" + DEFAULT_FROM_LUNCH);

        // Get all the workHistoryList where fromLunch not equals to UPDATED_FROM_LUNCH
        defaultWorkHistoryShouldBeFound("fromLunch.notEquals=" + UPDATED_FROM_LUNCH);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByFromLunchIsInShouldWork() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where fromLunch in DEFAULT_FROM_LUNCH or UPDATED_FROM_LUNCH
        defaultWorkHistoryShouldBeFound("fromLunch.in=" + DEFAULT_FROM_LUNCH + "," + UPDATED_FROM_LUNCH);

        // Get all the workHistoryList where fromLunch equals to UPDATED_FROM_LUNCH
        defaultWorkHistoryShouldNotBeFound("fromLunch.in=" + UPDATED_FROM_LUNCH);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByFromLunchIsNullOrNotNull() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where fromLunch is not null
        defaultWorkHistoryShouldBeFound("fromLunch.specified=true");

        // Get all the workHistoryList where fromLunch is null
        defaultWorkHistoryShouldNotBeFound("fromLunch.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByEndIsEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where end equals to DEFAULT_END
        defaultWorkHistoryShouldBeFound("end.equals=" + DEFAULT_END);

        // Get all the workHistoryList where end equals to UPDATED_END
        defaultWorkHistoryShouldNotBeFound("end.equals=" + UPDATED_END);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByEndIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where end not equals to DEFAULT_END
        defaultWorkHistoryShouldNotBeFound("end.notEquals=" + DEFAULT_END);

        // Get all the workHistoryList where end not equals to UPDATED_END
        defaultWorkHistoryShouldBeFound("end.notEquals=" + UPDATED_END);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByEndIsInShouldWork() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where end in DEFAULT_END or UPDATED_END
        defaultWorkHistoryShouldBeFound("end.in=" + DEFAULT_END + "," + UPDATED_END);

        // Get all the workHistoryList where end equals to UPDATED_END
        defaultWorkHistoryShouldNotBeFound("end.in=" + UPDATED_END);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByEndIsNullOrNotNull() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where end is not null
        defaultWorkHistoryShouldBeFound("end.specified=true");

        // Get all the workHistoryList where end is null
        defaultWorkHistoryShouldNotBeFound("end.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByReasonDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where reasonDescription equals to DEFAULT_REASON_DESCRIPTION
        defaultWorkHistoryShouldBeFound("reasonDescription.equals=" + DEFAULT_REASON_DESCRIPTION);

        // Get all the workHistoryList where reasonDescription equals to UPDATED_REASON_DESCRIPTION
        defaultWorkHistoryShouldNotBeFound("reasonDescription.equals=" + UPDATED_REASON_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByReasonDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where reasonDescription not equals to DEFAULT_REASON_DESCRIPTION
        defaultWorkHistoryShouldNotBeFound("reasonDescription.notEquals=" + DEFAULT_REASON_DESCRIPTION);

        // Get all the workHistoryList where reasonDescription not equals to UPDATED_REASON_DESCRIPTION
        defaultWorkHistoryShouldBeFound("reasonDescription.notEquals=" + UPDATED_REASON_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByReasonDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where reasonDescription in DEFAULT_REASON_DESCRIPTION or UPDATED_REASON_DESCRIPTION
        defaultWorkHistoryShouldBeFound("reasonDescription.in=" + DEFAULT_REASON_DESCRIPTION + "," + UPDATED_REASON_DESCRIPTION);

        // Get all the workHistoryList where reasonDescription equals to UPDATED_REASON_DESCRIPTION
        defaultWorkHistoryShouldNotBeFound("reasonDescription.in=" + UPDATED_REASON_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByReasonDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where reasonDescription is not null
        defaultWorkHistoryShouldBeFound("reasonDescription.specified=true");

        // Get all the workHistoryList where reasonDescription is null
        defaultWorkHistoryShouldNotBeFound("reasonDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByReasonDescriptionContainsSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where reasonDescription contains DEFAULT_REASON_DESCRIPTION
        defaultWorkHistoryShouldBeFound("reasonDescription.contains=" + DEFAULT_REASON_DESCRIPTION);

        // Get all the workHistoryList where reasonDescription contains UPDATED_REASON_DESCRIPTION
        defaultWorkHistoryShouldNotBeFound("reasonDescription.contains=" + UPDATED_REASON_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByReasonDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where reasonDescription does not contain DEFAULT_REASON_DESCRIPTION
        defaultWorkHistoryShouldNotBeFound("reasonDescription.doesNotContain=" + DEFAULT_REASON_DESCRIPTION);

        // Get all the workHistoryList where reasonDescription does not contain UPDATED_REASON_DESCRIPTION
        defaultWorkHistoryShouldBeFound("reasonDescription.doesNotContain=" + UPDATED_REASON_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where status equals to DEFAULT_STATUS
        defaultWorkHistoryShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the workHistoryList where status equals to UPDATED_STATUS
        defaultWorkHistoryShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where status not equals to DEFAULT_STATUS
        defaultWorkHistoryShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the workHistoryList where status not equals to UPDATED_STATUS
        defaultWorkHistoryShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultWorkHistoryShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the workHistoryList where status equals to UPDATED_STATUS
        defaultWorkHistoryShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        // Get all the workHistoryList where status is not null
        defaultWorkHistoryShouldBeFound("status.specified=true");

        // Get all the workHistoryList where status is null
        defaultWorkHistoryShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByWorkerIsEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);
        Worker worker;
        if (TestUtil.findAll(em, Worker.class).isEmpty()) {
            worker = WorkerResourceIT.createEntity(em);
            em.persist(worker);
            em.flush();
        } else {
            worker = TestUtil.findAll(em, Worker.class).get(0);
        }
        em.persist(worker);
        em.flush();
        workHistory.setWorker(worker);
        workHistoryRepository.saveAndFlush(workHistory);
        Long workerId = worker.getId();

        // Get all the workHistoryList where worker equals to workerId
        defaultWorkHistoryShouldBeFound("workerId.equals=" + workerId);

        // Get all the workHistoryList where worker equals to (workerId + 1)
        defaultWorkHistoryShouldNotBeFound("workerId.equals=" + (workerId + 1));
    }

    @Test
    @Transactional
    void getAllWorkHistoriesByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);
        Reason reason;
        if (TestUtil.findAll(em, Reason.class).isEmpty()) {
            reason = ReasonResourceIT.createEntity(em);
            em.persist(reason);
            em.flush();
        } else {
            reason = TestUtil.findAll(em, Reason.class).get(0);
        }
        em.persist(reason);
        em.flush();
        workHistory.setReason(reason);
        workHistoryRepository.saveAndFlush(workHistory);
        Long reasonId = reason.getId();

        // Get all the workHistoryList where reason equals to reasonId
        defaultWorkHistoryShouldBeFound("reasonId.equals=" + reasonId);

        // Get all the workHistoryList where reason equals to (reasonId + 1)
        defaultWorkHistoryShouldNotBeFound("reasonId.equals=" + (reasonId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWorkHistoryShouldBeFound(String filter) throws Exception {
        restWorkHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].toLunch").value(hasItem(DEFAULT_TO_LUNCH.toString())))
            .andExpect(jsonPath("$.[*].fromLunch").value(hasItem(DEFAULT_FROM_LUNCH.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())))
            .andExpect(jsonPath("$.[*].reasonDescription").value(hasItem(DEFAULT_REASON_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restWorkHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWorkHistoryShouldNotBeFound(String filter) throws Exception {
        restWorkHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWorkHistoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWorkHistory() throws Exception {
        // Get the workHistory
        restWorkHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWorkHistory() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        int databaseSizeBeforeUpdate = workHistoryRepository.findAll().size();

        // Update the workHistory
        WorkHistory updatedWorkHistory = workHistoryRepository.findById(workHistory.getId()).get();
        // Disconnect from session so that the updates on updatedWorkHistory are not directly saved in db
        em.detach(updatedWorkHistory);
        updatedWorkHistory
            .start(UPDATED_START)
            .toLunch(UPDATED_TO_LUNCH)
            .fromLunch(UPDATED_FROM_LUNCH)
            .end(UPDATED_END)
            .reasonDescription(UPDATED_REASON_DESCRIPTION)
            .status(UPDATED_STATUS);
        WorkHistoryDTO workHistoryDTO = workHistoryMapper.toDto(updatedWorkHistory);

        restWorkHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeUpdate);
        WorkHistory testWorkHistory = workHistoryList.get(workHistoryList.size() - 1);
        assertThat(testWorkHistory.getStart()).isEqualTo(UPDATED_START);
        assertThat(testWorkHistory.getToLunch()).isEqualTo(UPDATED_TO_LUNCH);
        assertThat(testWorkHistory.getFromLunch()).isEqualTo(UPDATED_FROM_LUNCH);
        assertThat(testWorkHistory.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testWorkHistory.getReasonDescription()).isEqualTo(UPDATED_REASON_DESCRIPTION);
        assertThat(testWorkHistory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingWorkHistory() throws Exception {
        int databaseSizeBeforeUpdate = workHistoryRepository.findAll().size();
        workHistory.setId(count.incrementAndGet());

        // Create the WorkHistory
        WorkHistoryDTO workHistoryDTO = workHistoryMapper.toDto(workHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkHistory() throws Exception {
        int databaseSizeBeforeUpdate = workHistoryRepository.findAll().size();
        workHistory.setId(count.incrementAndGet());

        // Create the WorkHistory
        WorkHistoryDTO workHistoryDTO = workHistoryMapper.toDto(workHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkHistory() throws Exception {
        int databaseSizeBeforeUpdate = workHistoryRepository.findAll().size();
        workHistory.setId(count.incrementAndGet());

        // Create the WorkHistory
        WorkHistoryDTO workHistoryDTO = workHistoryMapper.toDto(workHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWorkHistoryWithPatch() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        int databaseSizeBeforeUpdate = workHistoryRepository.findAll().size();

        // Update the workHistory using partial update
        WorkHistory partialUpdatedWorkHistory = new WorkHistory();
        partialUpdatedWorkHistory.setId(workHistory.getId());

        partialUpdatedWorkHistory.end(UPDATED_END).reasonDescription(UPDATED_REASON_DESCRIPTION).status(UPDATED_STATUS);

        restWorkHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkHistory))
            )
            .andExpect(status().isOk());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeUpdate);
        WorkHistory testWorkHistory = workHistoryList.get(workHistoryList.size() - 1);
        assertThat(testWorkHistory.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testWorkHistory.getToLunch()).isEqualTo(DEFAULT_TO_LUNCH);
        assertThat(testWorkHistory.getFromLunch()).isEqualTo(DEFAULT_FROM_LUNCH);
        assertThat(testWorkHistory.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testWorkHistory.getReasonDescription()).isEqualTo(UPDATED_REASON_DESCRIPTION);
        assertThat(testWorkHistory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateWorkHistoryWithPatch() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        int databaseSizeBeforeUpdate = workHistoryRepository.findAll().size();

        // Update the workHistory using partial update
        WorkHistory partialUpdatedWorkHistory = new WorkHistory();
        partialUpdatedWorkHistory.setId(workHistory.getId());

        partialUpdatedWorkHistory
            .start(UPDATED_START)
            .toLunch(UPDATED_TO_LUNCH)
            .fromLunch(UPDATED_FROM_LUNCH)
            .end(UPDATED_END)
            .reasonDescription(UPDATED_REASON_DESCRIPTION)
            .status(UPDATED_STATUS);

        restWorkHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkHistory))
            )
            .andExpect(status().isOk());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeUpdate);
        WorkHistory testWorkHistory = workHistoryList.get(workHistoryList.size() - 1);
        assertThat(testWorkHistory.getStart()).isEqualTo(UPDATED_START);
        assertThat(testWorkHistory.getToLunch()).isEqualTo(UPDATED_TO_LUNCH);
        assertThat(testWorkHistory.getFromLunch()).isEqualTo(UPDATED_FROM_LUNCH);
        assertThat(testWorkHistory.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testWorkHistory.getReasonDescription()).isEqualTo(UPDATED_REASON_DESCRIPTION);
        assertThat(testWorkHistory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingWorkHistory() throws Exception {
        int databaseSizeBeforeUpdate = workHistoryRepository.findAll().size();
        workHistory.setId(count.incrementAndGet());

        // Create the WorkHistory
        WorkHistoryDTO workHistoryDTO = workHistoryMapper.toDto(workHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkHistory() throws Exception {
        int databaseSizeBeforeUpdate = workHistoryRepository.findAll().size();
        workHistory.setId(count.incrementAndGet());

        // Create the WorkHistory
        WorkHistoryDTO workHistoryDTO = workHistoryMapper.toDto(workHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkHistory() throws Exception {
        int databaseSizeBeforeUpdate = workHistoryRepository.findAll().size();
        workHistory.setId(count.incrementAndGet());

        // Create the WorkHistory
        WorkHistoryDTO workHistoryDTO = workHistoryMapper.toDto(workHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(workHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkHistory in the database
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWorkHistory() throws Exception {
        // Initialize the database
        workHistoryRepository.saveAndFlush(workHistory);

        int databaseSizeBeforeDelete = workHistoryRepository.findAll().size();

        // Delete the workHistory
        restWorkHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, workHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WorkHistory> workHistoryList = workHistoryRepository.findAll();
        assertThat(workHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
