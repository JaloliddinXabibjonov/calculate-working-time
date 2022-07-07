package uz.devops.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.IntegrationTest;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.Worker;
import uz.devops.repository.WorkerRepository;
import uz.devops.service.WorkerService;
import uz.devops.service.dto.WorkerDTO;
import uz.devops.service.mapper.WorkerMapper;

/**
 * Integration tests for the {@link WorkerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WorkerResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_WORKER_TG_ID = 1L;
    private static final Long UPDATED_WORKER_TG_ID = 2L;
    private static final Long SMALLER_WORKER_TG_ID = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/workers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkerRepository workerRepository;

    @Mock
    private WorkerRepository workerRepositoryMock;

    @Autowired
    private WorkerMapper workerMapper;

    @Mock
    private WorkerService workerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkerMockMvc;

    private Worker worker;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Worker createEntity(EntityManager em) {
        Worker worker = new Worker().fullName(DEFAULT_FULL_NAME).workerTgId(DEFAULT_WORKER_TG_ID);
        return worker;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Worker createUpdatedEntity(EntityManager em) {
        Worker worker = new Worker().fullName(UPDATED_FULL_NAME).workerTgId(UPDATED_WORKER_TG_ID);
        return worker;
    }

    @BeforeEach
    public void initTest() {
        worker = createEntity(em);
    }

    @Test
    @Transactional
    void createWorker() throws Exception {
        int databaseSizeBeforeCreate = workerRepository.findAll().size();
        // Create the Worker
        WorkerDTO workerDTO = workerMapper.toDto(worker);
        restWorkerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workerDTO)))
            .andExpect(status().isCreated());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeCreate + 1);
        Worker testWorker = workerList.get(workerList.size() - 1);
        assertThat(testWorker.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testWorker.getWorkerTgId()).isEqualTo(DEFAULT_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void createWorkerWithExistingId() throws Exception {
        // Create the Worker with an existing ID
        worker.setId(1L);
        WorkerDTO workerDTO = workerMapper.toDto(worker);

        int databaseSizeBeforeCreate = workerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = workerRepository.findAll().size();
        // set the field null
        worker.setFullName(null);

        // Create the Worker, which fails.
        WorkerDTO workerDTO = workerMapper.toDto(worker);

        restWorkerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workerDTO)))
            .andExpect(status().isBadRequest());

        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWorkerTgIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = workerRepository.findAll().size();
        // set the field null
        worker.setWorkerTgId(null);

        // Create the Worker, which fails.
        WorkerDTO workerDTO = workerMapper.toDto(worker);

        restWorkerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workerDTO)))
            .andExpect(status().isBadRequest());

        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWorkers() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList
        restWorkerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(worker.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].workerTgId").value(hasItem(DEFAULT_WORKER_TG_ID.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkersWithEagerRelationshipsIsEnabled() throws Exception {
        when(workerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(workerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(workerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(workerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getWorker() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get the worker
        restWorkerMockMvc
            .perform(get(ENTITY_API_URL_ID, worker.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(worker.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.workerTgId").value(DEFAULT_WORKER_TG_ID.intValue()));
    }

    @Test
    @Transactional
    void getWorkersByIdFiltering() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        Long id = worker.getId();

        defaultWorkerShouldBeFound("id.equals=" + id);
        defaultWorkerShouldNotBeFound("id.notEquals=" + id);

        defaultWorkerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWorkerShouldNotBeFound("id.greaterThan=" + id);

        defaultWorkerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWorkerShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWorkersByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where fullName equals to DEFAULT_FULL_NAME
        defaultWorkerShouldBeFound("fullName.equals=" + DEFAULT_FULL_NAME);

        // Get all the workerList where fullName equals to UPDATED_FULL_NAME
        defaultWorkerShouldNotBeFound("fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWorkersByFullNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where fullName not equals to DEFAULT_FULL_NAME
        defaultWorkerShouldNotBeFound("fullName.notEquals=" + DEFAULT_FULL_NAME);

        // Get all the workerList where fullName not equals to UPDATED_FULL_NAME
        defaultWorkerShouldBeFound("fullName.notEquals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWorkersByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where fullName in DEFAULT_FULL_NAME or UPDATED_FULL_NAME
        defaultWorkerShouldBeFound("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME);

        // Get all the workerList where fullName equals to UPDATED_FULL_NAME
        defaultWorkerShouldNotBeFound("fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWorkersByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where fullName is not null
        defaultWorkerShouldBeFound("fullName.specified=true");

        // Get all the workerList where fullName is null
        defaultWorkerShouldNotBeFound("fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkersByFullNameContainsSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where fullName contains DEFAULT_FULL_NAME
        defaultWorkerShouldBeFound("fullName.contains=" + DEFAULT_FULL_NAME);

        // Get all the workerList where fullName contains UPDATED_FULL_NAME
        defaultWorkerShouldNotBeFound("fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWorkersByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where fullName does not contain DEFAULT_FULL_NAME
        defaultWorkerShouldNotBeFound("fullName.doesNotContain=" + DEFAULT_FULL_NAME);

        // Get all the workerList where fullName does not contain UPDATED_FULL_NAME
        defaultWorkerShouldBeFound("fullName.doesNotContain=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllWorkersByWorkerTgIdIsEqualToSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where workerTgId equals to DEFAULT_WORKER_TG_ID
        defaultWorkerShouldBeFound("workerTgId.equals=" + DEFAULT_WORKER_TG_ID);

        // Get all the workerList where workerTgId equals to UPDATED_WORKER_TG_ID
        defaultWorkerShouldNotBeFound("workerTgId.equals=" + UPDATED_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void getAllWorkersByWorkerTgIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where workerTgId not equals to DEFAULT_WORKER_TG_ID
        defaultWorkerShouldNotBeFound("workerTgId.notEquals=" + DEFAULT_WORKER_TG_ID);

        // Get all the workerList where workerTgId not equals to UPDATED_WORKER_TG_ID
        defaultWorkerShouldBeFound("workerTgId.notEquals=" + UPDATED_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void getAllWorkersByWorkerTgIdIsInShouldWork() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where workerTgId in DEFAULT_WORKER_TG_ID or UPDATED_WORKER_TG_ID
        defaultWorkerShouldBeFound("workerTgId.in=" + DEFAULT_WORKER_TG_ID + "," + UPDATED_WORKER_TG_ID);

        // Get all the workerList where workerTgId equals to UPDATED_WORKER_TG_ID
        defaultWorkerShouldNotBeFound("workerTgId.in=" + UPDATED_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void getAllWorkersByWorkerTgIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where workerTgId is not null
        defaultWorkerShouldBeFound("workerTgId.specified=true");

        // Get all the workerList where workerTgId is null
        defaultWorkerShouldNotBeFound("workerTgId.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkersByWorkerTgIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where workerTgId is greater than or equal to DEFAULT_WORKER_TG_ID
        defaultWorkerShouldBeFound("workerTgId.greaterThanOrEqual=" + DEFAULT_WORKER_TG_ID);

        // Get all the workerList where workerTgId is greater than or equal to UPDATED_WORKER_TG_ID
        defaultWorkerShouldNotBeFound("workerTgId.greaterThanOrEqual=" + UPDATED_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void getAllWorkersByWorkerTgIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where workerTgId is less than or equal to DEFAULT_WORKER_TG_ID
        defaultWorkerShouldBeFound("workerTgId.lessThanOrEqual=" + DEFAULT_WORKER_TG_ID);

        // Get all the workerList where workerTgId is less than or equal to SMALLER_WORKER_TG_ID
        defaultWorkerShouldNotBeFound("workerTgId.lessThanOrEqual=" + SMALLER_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void getAllWorkersByWorkerTgIdIsLessThanSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where workerTgId is less than DEFAULT_WORKER_TG_ID
        defaultWorkerShouldNotBeFound("workerTgId.lessThan=" + DEFAULT_WORKER_TG_ID);

        // Get all the workerList where workerTgId is less than UPDATED_WORKER_TG_ID
        defaultWorkerShouldBeFound("workerTgId.lessThan=" + UPDATED_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void getAllWorkersByWorkerTgIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workerList where workerTgId is greater than DEFAULT_WORKER_TG_ID
        defaultWorkerShouldNotBeFound("workerTgId.greaterThan=" + DEFAULT_WORKER_TG_ID);

        // Get all the workerList where workerTgId is greater than SMALLER_WORKER_TG_ID
        defaultWorkerShouldBeFound("workerTgId.greaterThan=" + SMALLER_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void getAllWorkersByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);
        Role role;
        if (TestUtil.findAll(em, Role.class).isEmpty()) {
            role = RoleResourceIT.createEntity(em);
            em.persist(role);
            em.flush();
        } else {
            role = TestUtil.findAll(em, Role.class).get(0);
        }
        em.persist(role);
        em.flush();
        worker.addRole(role);
        workerRepository.saveAndFlush(worker);
        Long roleId = role.getId();

        // Get all the workerList where role equals to roleId
        defaultWorkerShouldBeFound("roleId.equals=" + roleId);

        // Get all the workerList where role equals to (roleId + 1)
        defaultWorkerShouldNotBeFound("roleId.equals=" + (roleId + 1));
    }

    @Test
    @Transactional
    void getAllWorkersByWorkHistoryIsEqualToSomething() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);
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
        worker.addWorkHistory(workHistory);
        workerRepository.saveAndFlush(worker);
        Long workHistoryId = workHistory.getId();

        // Get all the workerList where workHistory equals to workHistoryId
        defaultWorkerShouldBeFound("workHistoryId.equals=" + workHistoryId);

        // Get all the workerList where workHistory equals to (workHistoryId + 1)
        defaultWorkerShouldNotBeFound("workHistoryId.equals=" + (workHistoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWorkerShouldBeFound(String filter) throws Exception {
        restWorkerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(worker.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].workerTgId").value(hasItem(DEFAULT_WORKER_TG_ID.intValue())));

        // Check, that the count call also returns 1
        restWorkerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWorkerShouldNotBeFound(String filter) throws Exception {
        restWorkerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWorkerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWorker() throws Exception {
        // Get the worker
        restWorkerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWorker() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        int databaseSizeBeforeUpdate = workerRepository.findAll().size();

        // Update the worker
        Worker updatedWorker = workerRepository.findById(worker.getId()).get();
        // Disconnect from session so that the updates on updatedWorker are not directly saved in db
        em.detach(updatedWorker);
        updatedWorker.fullName(UPDATED_FULL_NAME).workerTgId(UPDATED_WORKER_TG_ID);
        WorkerDTO workerDTO = workerMapper.toDto(updatedWorker);

        restWorkerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeUpdate);
        Worker testWorker = workerList.get(workerList.size() - 1);
        assertThat(testWorker.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testWorker.getWorkerTgId()).isEqualTo(UPDATED_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void putNonExistingWorker() throws Exception {
        int databaseSizeBeforeUpdate = workerRepository.findAll().size();
        worker.setId(count.incrementAndGet());

        // Create the Worker
        WorkerDTO workerDTO = workerMapper.toDto(worker);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorker() throws Exception {
        int databaseSizeBeforeUpdate = workerRepository.findAll().size();
        worker.setId(count.incrementAndGet());

        // Create the Worker
        WorkerDTO workerDTO = workerMapper.toDto(worker);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorker() throws Exception {
        int databaseSizeBeforeUpdate = workerRepository.findAll().size();
        worker.setId(count.incrementAndGet());

        // Create the Worker
        WorkerDTO workerDTO = workerMapper.toDto(worker);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWorkerWithPatch() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        int databaseSizeBeforeUpdate = workerRepository.findAll().size();

        // Update the worker using partial update
        Worker partialUpdatedWorker = new Worker();
        partialUpdatedWorker.setId(worker.getId());

        partialUpdatedWorker.fullName(UPDATED_FULL_NAME);

        restWorkerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorker.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorker))
            )
            .andExpect(status().isOk());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeUpdate);
        Worker testWorker = workerList.get(workerList.size() - 1);
        assertThat(testWorker.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testWorker.getWorkerTgId()).isEqualTo(DEFAULT_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void fullUpdateWorkerWithPatch() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        int databaseSizeBeforeUpdate = workerRepository.findAll().size();

        // Update the worker using partial update
        Worker partialUpdatedWorker = new Worker();
        partialUpdatedWorker.setId(worker.getId());

        partialUpdatedWorker.fullName(UPDATED_FULL_NAME).workerTgId(UPDATED_WORKER_TG_ID);

        restWorkerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorker.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorker))
            )
            .andExpect(status().isOk());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeUpdate);
        Worker testWorker = workerList.get(workerList.size() - 1);
        assertThat(testWorker.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testWorker.getWorkerTgId()).isEqualTo(UPDATED_WORKER_TG_ID);
    }

    @Test
    @Transactional
    void patchNonExistingWorker() throws Exception {
        int databaseSizeBeforeUpdate = workerRepository.findAll().size();
        worker.setId(count.incrementAndGet());

        // Create the Worker
        WorkerDTO workerDTO = workerMapper.toDto(worker);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorker() throws Exception {
        int databaseSizeBeforeUpdate = workerRepository.findAll().size();
        worker.setId(count.incrementAndGet());

        // Create the Worker
        WorkerDTO workerDTO = workerMapper.toDto(worker);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorker() throws Exception {
        int databaseSizeBeforeUpdate = workerRepository.findAll().size();
        worker.setId(count.incrementAndGet());

        // Create the Worker
        WorkerDTO workerDTO = workerMapper.toDto(worker);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(workerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Worker in the database
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWorker() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        int databaseSizeBeforeDelete = workerRepository.findAll().size();

        // Delete the worker
        restWorkerMockMvc
            .perform(delete(ENTITY_API_URL_ID, worker.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Worker> workerList = workerRepository.findAll();
        assertThat(workerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
