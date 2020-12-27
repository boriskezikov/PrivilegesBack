package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.stereotype.Service;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.dto.FindPrivilegeDTO;
import ru.hse.web.dto.PrivilegeDto;
import ru.hse.web.mapper.PrivilegeMapper;
import ru.hse.web.model.Rule;
import ru.hse.web.repository.PrivilegeRepository;
import ru.hse.web.repository.UserRepository;
import ru.hse.web.repository.custom.PrivilegeRepositoryImpl;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.hse.web.service.Utils.buildFullName;

@RequiredArgsConstructor
@Service
@Slf4j
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeRepositoryImpl privilegeRepositoryCustom;
    private final UserRepository userRepository;
    private final PrivilegeMapper mapper;
    private final SMTPService smtpService;

    public PrivilegeEntity createPrivilege(PrivilegeDto dto) {
        PrivilegeEntity privilegeEntity = PrivilegeEntity.builder()
                .availableForAssignment(dto.isAvailableForAssignment())
                .legalMinistry(dto.getLegalMinistry())
                .description(dto.getDescription())
                .name(dto.getName())
                .gradesRequired(dto.getGradesRequired())
                .build();
        var privilege = privilegeRepository.save(privilegeEntity);
        log.info("Privilege created");
        return privilege;
    }

    public PrivilegeEntity updatePrivilege(PrivilegeDto dto) {
        var privilege = privilegeRepository.findById(dto.getId()).orElseThrow(EntityNotFoundException::new);
        mapper.updateEntityFromDto(dto, privilege);
        var updated = privilegeRepository.save(privilege);
        log.info("Privilege updated");
        notifyAllAssignedUsers(updated);
        return updated;
    }

    public List<PrivilegeEntity> find(FindPrivilegeDTO findPrivilegeDTO) {
        List<PrivilegeEntity> privileges;
        if (findPrivilegeDTO.getCriteria() == null) {
            privileges = privilegeRepository.findAll();
        } else {
            privileges = privilegeRepositoryCustom.find(findPrivilegeDTO.getCriteria(), findPrivilegeDTO.getSort());
        }
        return privileges;
    }

    public List<Rule> getAvailableRules() {
        return Arrays.asList(Rule.values());
    }

    public List<PrivilegeEntity> findAll() {
        return privilegeRepository.findAll();
    }

    public void removeById(BigInteger id) {
        privilegeRepository.deleteById(id);
    }

    public PrivilegeEntity getById(BigInteger id) {
        return privilegeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }


    private void notifyAllAssignedUsers(PrivilegeEntity privilegeEntity) {
        var toNotify = userRepository.findAll().stream().filter(user -> {
            var privileges = user.getPrivileges().stream().map(PrivilegeEntity::getId).collect(Collectors.toList());
            return privileges.contains(privilegeEntity.getId());
        }).collect(Collectors.toList());
        toNotify.forEach(user -> smtpService.sendPrivilegeUpdatedNotification(user.getPrimaryEmail(), buildFullName(user.getFirstName(), user.getLastName()), privilegeEntity.getName()));
    }
}
