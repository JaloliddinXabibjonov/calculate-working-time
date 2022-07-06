package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.Action;
import uz.devops.domain.Reason;
import uz.devops.service.dto.ActionDTO;
import uz.devops.service.dto.ReasonDTO;

/**
 * Mapper for the entity {@link Action} and its DTO {@link ActionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ActionMapper extends EntityMapper<ActionDTO, Action> {
    @Mapping(target = "reason", source = "reason", qualifiedByName = "reasonId")
    ActionDTO toDto(Action s);

    @Named("reasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReasonDTO toDtoReasonId(Reason reason);
}
