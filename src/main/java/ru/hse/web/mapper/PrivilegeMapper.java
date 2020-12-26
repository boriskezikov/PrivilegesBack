package ru.hse.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.dto.PrivilegeDto;
import ru.hse.web.service.PrivilegeService;

@Mapper(uses = {PrivilegeService.class}, componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrivilegeMapper {

    void updateEntityFromDto(PrivilegeDto source, @MappingTarget PrivilegeEntity target);

}
