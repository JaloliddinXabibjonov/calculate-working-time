package uz.devops.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.domain.Action;
import uz.devops.repository.ActionRepository;
import uz.devops.service.ActionService;
import uz.devops.service.dto.ActionDTO;
import uz.devops.service.mapper.ActionMapper;

/**
 * Service Implementation for managing {@link Action}.
 */
@Service
@Transactional
public class ActionServiceImpl implements ActionService {

    private final Logger log = LoggerFactory.getLogger(ActionServiceImpl.class);

    private final ActionRepository actionRepository;

    private final ActionMapper actionMapper;

    public ActionServiceImpl(ActionRepository actionRepository, ActionMapper actionMapper) {
        this.actionRepository = actionRepository;
        this.actionMapper = actionMapper;
    }

    @Override
    public ActionDTO save(ActionDTO actionDTO) {
        log.debug("Request to save Action : {}", actionDTO);
        Action action = actionMapper.toEntity(actionDTO);
        action = actionRepository.save(action);
        return actionMapper.toDto(action);
    }

    @Override
    public ActionDTO update(ActionDTO actionDTO) {
        log.debug("Request to save Action : {}", actionDTO);
        Action action = actionMapper.toEntity(actionDTO);
        action = actionRepository.save(action);
        return actionMapper.toDto(action);
    }

    @Override
    public Optional<ActionDTO> partialUpdate(ActionDTO actionDTO) {
        log.debug("Request to partially update Action : {}", actionDTO);

        return actionRepository
            .findById(actionDTO.getId())
            .map(existingAction -> {
                actionMapper.partialUpdate(existingAction, actionDTO);

                return existingAction;
            })
            .map(actionRepository::save)
            .map(actionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Actions");
        return actionRepository.findAll(pageable).map(actionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActionDTO> findOne(Long id) {
        log.debug("Request to get Action : {}", id);
        return actionRepository.findById(id).map(actionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Action : {}", id);
        actionRepository.deleteById(id);
    }
}
