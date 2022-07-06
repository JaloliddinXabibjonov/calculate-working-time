package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.Reason;
import uz.devops.domain.WorkHistory;
import uz.devops.domain.Worker;
import uz.devops.service.dto.ReasonDTO;
import uz.devops.service.dto.WorkHistoryDTO;
import uz.devops.service.dto.WorkerDTO;

/**
 * Mapper for the entity {@link WorkHistory} and its DTO {@link WorkHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkHistoryMapper extends EntityMapper<WorkHistoryDTO, WorkHistory> {
    @Mapping(target = "worker", source = "worker", qualifiedByName = "workerId")
    @Mapping(target = "reason", source = "reason", qualifiedByName = "reasonId")
    WorkHistoryDTO toDto(WorkHistory s);

    @Named("workerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WorkerDTO toDtoWorkerId(Worker worker);

    @Named("reasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReasonDTO toDtoReasonId(Reason reason);
}
