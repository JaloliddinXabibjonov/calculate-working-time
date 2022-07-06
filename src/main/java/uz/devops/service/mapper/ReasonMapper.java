package uz.devops.service.mapper;

import org.mapstruct.*;
import uz.devops.domain.Reason;
import uz.devops.service.dto.ReasonDTO;

/**
 * Mapper for the entity {@link Reason} and its DTO {@link ReasonDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReasonMapper extends EntityMapper<ReasonDTO, Reason> {
    @Mapping(target = "parent", source = "parent", qualifiedByName = "reasonId")
    ReasonDTO toDto(Reason s);

    @Named("reasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ReasonDTO toDtoReasonId(Reason reason);
}
