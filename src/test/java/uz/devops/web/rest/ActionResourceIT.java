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
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.ActionRepository;
import uz.devops.service.dto.ActionDTO;
import uz.devops.service.mapper.ActionMapper;

/**
 * Integration tests for the {@link ActionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ActionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_COMMAND = "AAAAAAAAAA";
    private static final String UPDATED_COMMAND = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.WITH_REASON;
    private static final Status UPDATED_STATUS = Status.START_LUNCH;

    private static final String ENTITY_API_URL = "/api/actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ActionMapper actionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActionMockMvc;

    private Action action;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Action createEntity(EntityManager em) {
        Action action = new Action().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).command(DEFAULT_COMMAND).status(DEFAULT_STATUS);
        return action;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Action createUpdatedEntity(EntityManager em) {
        Action action = new Action().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).command(UPDATED_COMMAND).status(UPDATED_STATUS);
        return action;
    }

    @BeforeEach
    public void initTest() {
        action = createEntity(em);
    }

    @Test
    @Transactional
    void createAction() throws Exception {
        int databaseSizeBeforeCreate = actionRepository.findAll().size();
        // Create the Action
        ActionDTO actionDTO = actionMapper.toDto(action);
        restActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actionDTO)))
            .andExpect(status().isCreated());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeCreate + 1);
        Action testAction = actionList.get(actionList.size() - 1);
        assertThat(testAction.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAction.getCommand()).isEqualTo(DEFAULT_COMMAND);
        assertThat(testAction.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createActionWithExistingId() throws Exception {
        // Create the Action with an existing ID
        action.setId(1L);
        ActionDTO actionDTO = actionMapper.toDto(action);

        int databaseSizeBeforeCreate = actionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = actionRepository.findAll().size();
        // set the field null
        action.setName(null);

        // Create the Action, which fails.
        ActionDTO actionDTO = actionMapper.toDto(action);

        restActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actionDTO)))
            .andExpect(status().isBadRequest());

        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCommandIsRequired() throws Exception {
        int databaseSizeBeforeTest = actionRepository.findAll().size();
        // set the field null
        action.setCommand(null);

        // Create the Action, which fails.
        ActionDTO actionDTO = actionMapper.toDto(action);

        restActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actionDTO)))
            .andExpect(status().isBadRequest());

        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = actionRepository.findAll().size();
        // set the field null
        action.setStatus(null);

        // Create the Action, which fails.
        ActionDTO actionDTO = actionMapper.toDto(action);

        restActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actionDTO)))
            .andExpect(status().isBadRequest());

        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllActions() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList
        restActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(action.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].command").value(hasItem(DEFAULT_COMMAND)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getAction() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get the action
        restActionMockMvc
            .perform(get(ENTITY_API_URL_ID, action.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(action.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.command").value(DEFAULT_COMMAND))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getActionsByIdFiltering() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        Long id = action.getId();

        defaultActionShouldBeFound("id.equals=" + id);
        defaultActionShouldNotBeFound("id.notEquals=" + id);

        defaultActionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultActionShouldNotBeFound("id.greaterThan=" + id);

        defaultActionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultActionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllActionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where name equals to DEFAULT_NAME
        defaultActionShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the actionList where name equals to UPDATED_NAME
        defaultActionShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllActionsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where name not equals to DEFAULT_NAME
        defaultActionShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the actionList where name not equals to UPDATED_NAME
        defaultActionShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllActionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where name in DEFAULT_NAME or UPDATED_NAME
        defaultActionShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the actionList where name equals to UPDATED_NAME
        defaultActionShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllActionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where name is not null
        defaultActionShouldBeFound("name.specified=true");

        // Get all the actionList where name is null
        defaultActionShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllActionsByNameContainsSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where name contains DEFAULT_NAME
        defaultActionShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the actionList where name contains UPDATED_NAME
        defaultActionShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllActionsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where name does not contain DEFAULT_NAME
        defaultActionShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the actionList where name does not contain UPDATED_NAME
        defaultActionShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllActionsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where description equals to DEFAULT_DESCRIPTION
        defaultActionShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the actionList where description equals to UPDATED_DESCRIPTION
        defaultActionShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllActionsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where description not equals to DEFAULT_DESCRIPTION
        defaultActionShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the actionList where description not equals to UPDATED_DESCRIPTION
        defaultActionShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllActionsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultActionShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the actionList where description equals to UPDATED_DESCRIPTION
        defaultActionShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllActionsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where description is not null
        defaultActionShouldBeFound("description.specified=true");

        // Get all the actionList where description is null
        defaultActionShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllActionsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where description contains DEFAULT_DESCRIPTION
        defaultActionShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the actionList where description contains UPDATED_DESCRIPTION
        defaultActionShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllActionsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where description does not contain DEFAULT_DESCRIPTION
        defaultActionShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the actionList where description does not contain UPDATED_DESCRIPTION
        defaultActionShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllActionsByCommandIsEqualToSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where command equals to DEFAULT_COMMAND
        defaultActionShouldBeFound("command.equals=" + DEFAULT_COMMAND);

        // Get all the actionList where command equals to UPDATED_COMMAND
        defaultActionShouldNotBeFound("command.equals=" + UPDATED_COMMAND);
    }

    @Test
    @Transactional
    void getAllActionsByCommandIsNotEqualToSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where command not equals to DEFAULT_COMMAND
        defaultActionShouldNotBeFound("command.notEquals=" + DEFAULT_COMMAND);

        // Get all the actionList where command not equals to UPDATED_COMMAND
        defaultActionShouldBeFound("command.notEquals=" + UPDATED_COMMAND);
    }

    @Test
    @Transactional
    void getAllActionsByCommandIsInShouldWork() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where command in DEFAULT_COMMAND or UPDATED_COMMAND
        defaultActionShouldBeFound("command.in=" + DEFAULT_COMMAND + "," + UPDATED_COMMAND);

        // Get all the actionList where command equals to UPDATED_COMMAND
        defaultActionShouldNotBeFound("command.in=" + UPDATED_COMMAND);
    }

    @Test
    @Transactional
    void getAllActionsByCommandIsNullOrNotNull() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where command is not null
        defaultActionShouldBeFound("command.specified=true");

        // Get all the actionList where command is null
        defaultActionShouldNotBeFound("command.specified=false");
    }

    @Test
    @Transactional
    void getAllActionsByCommandContainsSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where command contains DEFAULT_COMMAND
        defaultActionShouldBeFound("command.contains=" + DEFAULT_COMMAND);

        // Get all the actionList where command contains UPDATED_COMMAND
        defaultActionShouldNotBeFound("command.contains=" + UPDATED_COMMAND);
    }

    @Test
    @Transactional
    void getAllActionsByCommandNotContainsSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where command does not contain DEFAULT_COMMAND
        defaultActionShouldNotBeFound("command.doesNotContain=" + DEFAULT_COMMAND);

        // Get all the actionList where command does not contain UPDATED_COMMAND
        defaultActionShouldBeFound("command.doesNotContain=" + UPDATED_COMMAND);
    }

    @Test
    @Transactional
    void getAllActionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where status equals to DEFAULT_STATUS
        defaultActionShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the actionList where status equals to UPDATED_STATUS
        defaultActionShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllActionsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where status not equals to DEFAULT_STATUS
        defaultActionShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the actionList where status not equals to UPDATED_STATUS
        defaultActionShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllActionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultActionShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the actionList where status equals to UPDATED_STATUS
        defaultActionShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllActionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actionList where status is not null
        defaultActionShouldBeFound("status.specified=true");

        // Get all the actionList where status is null
        defaultActionShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllActionsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);
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
        action.setReason(reason);
        actionRepository.saveAndFlush(action);
        Long reasonId = reason.getId();

        // Get all the actionList where reason equals to reasonId
        defaultActionShouldBeFound("reasonId.equals=" + reasonId);

        // Get all the actionList where reason equals to (reasonId + 1)
        defaultActionShouldNotBeFound("reasonId.equals=" + (reasonId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultActionShouldBeFound(String filter) throws Exception {
        restActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(action.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].command").value(hasItem(DEFAULT_COMMAND)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultActionShouldNotBeFound(String filter) throws Exception {
        restActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAction() throws Exception {
        // Get the action
        restActionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAction() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        int databaseSizeBeforeUpdate = actionRepository.findAll().size();

        // Update the action
        Action updatedAction = actionRepository.findById(action.getId()).get();
        // Disconnect from session so that the updates on updatedAction are not directly saved in db
        em.detach(updatedAction);
        updatedAction.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).command(UPDATED_COMMAND).status(UPDATED_STATUS);
        ActionDTO actionDTO = actionMapper.toDto(updatedAction);

        restActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
        Action testAction = actionList.get(actionList.size() - 1);
        assertThat(testAction.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAction.getCommand()).isEqualTo(UPDATED_COMMAND);
        assertThat(testAction.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // Create the Action
        ActionDTO actionDTO = actionMapper.toDto(action);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // Create the Action
        ActionDTO actionDTO = actionMapper.toDto(action);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // Create the Action
        ActionDTO actionDTO = actionMapper.toDto(action);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateActionWithPatch() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        int databaseSizeBeforeUpdate = actionRepository.findAll().size();

        // Update the action using partial update
        Action partialUpdatedAction = new Action();
        partialUpdatedAction.setId(action.getId());

        partialUpdatedAction.name(UPDATED_NAME).command(UPDATED_COMMAND);

        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAction))
            )
            .andExpect(status().isOk());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
        Action testAction = actionList.get(actionList.size() - 1);
        assertThat(testAction.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAction.getCommand()).isEqualTo(UPDATED_COMMAND);
        assertThat(testAction.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateActionWithPatch() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        int databaseSizeBeforeUpdate = actionRepository.findAll().size();

        // Update the action using partial update
        Action partialUpdatedAction = new Action();
        partialUpdatedAction.setId(action.getId());

        partialUpdatedAction.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).command(UPDATED_COMMAND).status(UPDATED_STATUS);

        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAction))
            )
            .andExpect(status().isOk());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
        Action testAction = actionList.get(actionList.size() - 1);
        assertThat(testAction.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAction.getCommand()).isEqualTo(UPDATED_COMMAND);
        assertThat(testAction.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // Create the Action
        ActionDTO actionDTO = actionMapper.toDto(action);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, actionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // Create the Action
        ActionDTO actionDTO = actionMapper.toDto(action);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAction() throws Exception {
        int databaseSizeBeforeUpdate = actionRepository.findAll().size();
        action.setId(count.incrementAndGet());

        // Create the Action
        ActionDTO actionDTO = actionMapper.toDto(action);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(actionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Action in the database
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAction() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        int databaseSizeBeforeDelete = actionRepository.findAll().size();

        // Delete the action
        restActionMockMvc
            .perform(delete(ENTITY_API_URL_ID, action.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Action> actionList = actionRepository.findAll();
        assertThat(actionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
