package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.dto.FindPrivilegeDTO;
import ru.hse.web.dto.PrivilegeDto;
import ru.hse.web.mapper.PrivilegeMapper;
import ru.hse.web.model.Rule;
import ru.hse.web.repository.PrivilegeRepository;
import ru.hse.web.repository.custom.PrivilegeRepositoryImpl;

import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeRepositoryImpl privilegeRepositoryCustom;
    private final PrivilegeMapper mapper;

    public PrivilegeEntity createPrivilege(PrivilegeDto dto) {
        PrivilegeEntity privilegeEntity = PrivilegeEntity.builder()
                .availableForAssignment(dto.isAvailableForAssignment())
                .legalMinistry(dto.getLegalMinistry())
                .text(dto.getText())
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
        return updated;
    }

    public void removeById(BigInteger id){
        privilegeRepository.deleteById(id);
    }

    public PrivilegeEntity getById(BigInteger id) {
        return privilegeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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

    public List<Rule> getAvailableRules(){
        return Arrays.asList(Rule.values());
    }

    public List<PrivilegeEntity> findAll() {
        return privilegeRepository.findAll();
    }
}
