package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hse.web.domain.AssignmentEntity;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.dto.AssignPrivilegeDto;
import ru.hse.web.dto.CreateAssignmentDto;
import ru.hse.web.dto.FindAssignmentDto;
import ru.hse.web.dto.FindPrivilegeDTO;
import ru.hse.web.dto.MoveAssignmentDto;
import ru.hse.web.dto.PrivilegeDto;
import ru.hse.web.mapper.PrivilegeMapper;
import ru.hse.web.model.AssignmentStatus;
import ru.hse.web.model.Rule;
import ru.hse.web.repository.AssignmentRepository;
import ru.hse.web.repository.PrivilegeRepository;
import ru.hse.web.repository.UserRepository;
import ru.hse.web.repository.custom.AssignmentRepositoryImpl;
import ru.hse.web.repository.custom.PrivilegeRepositoryImpl;

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
    private final AssignmentRepository assignmentRepository;
    private final AssignmentRepositoryImpl assignmentRepositoryCustom;
    private final UserDetailsService userDetailsService;
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

    public List<PrivilegeEntity> findPrivilege(FindPrivilegeDTO findPrivilegeDTO) {
        List<PrivilegeEntity> privileges;
        if (findPrivilegeDTO == null) {
            privileges = privilegeRepository.findAll();
            privileges.removeIf(item -> (!item.isAvailableForAssignment()));
        } else {
            privileges = privilegeRepositoryCustom.find(findPrivilegeDTO.getCriteria(), findPrivilegeDTO.getSort());
        }
        return privileges;
    }

    public AssignmentEntity createAssignment(CreateAssignmentDto createAssignmentDto) {
        var assignment = assignmentRepository.findByUser_IdAndPrivilege_Id(createAssignmentDto.getUserId(), createAssignmentDto.getPrivilegeId());
        if (assignment.isPresent()) {
            return assignment.get();
        } else {
            var user = userRepository.findById(createAssignmentDto.getUserId()).orElseThrow(EntityNotFoundException::new);
            var privilege = privilegeRepository.findById(createAssignmentDto.getPrivilegeId()).orElseThrow(EntityNotFoundException::new);
            AssignmentEntity newAssignment = AssignmentEntity.builder()
                    .privilege(privilege)
                    .user(user)
                    .build();
            var saved = assignmentRepository.save(newAssignment);
            smtpService.sendAssignmentStatusUpdated(user.getPrimaryEmail(), buildFullName(user.getFirstName(), user.getLastName()), privilege, saved.getAssignmentStatus());
            return saved;
        }
    }

    public AssignmentEntity moveAssignment(MoveAssignmentDto moveAssignmentDto) {
        var assignment = assignmentRepository.findById(moveAssignmentDto.getAssignmentId()).orElseThrow(EntityNotFoundException::new);
        assignment.setAssignmentStatus(moveAssignmentDto.getStatus());
        var saved = assignmentRepository.save(assignment);
        var user = userRepository.findById(saved.getUser().getId()).orElseThrow(EntityNotFoundException::new);
        var privilege = privilegeRepository.findById(saved.getPrivilege().getId()).orElseThrow(EntityNotFoundException::new);
        smtpService.sendAssignmentStatusUpdated(user.getPrimaryEmail(), buildFullName(user.getFirstName(), user.getLastName()), privilege, saved.getAssignmentStatus());
        if (saved.getAssignmentStatus().equals(AssignmentStatus.APPROVED)){
            userDetailsService.assignPrivilege(AssignPrivilegeDto.builder().userId(user.getId()).privilegeId(privilege.getId()).build());
        }
        return saved;
    }

    public List<AssignmentEntity> findAssignment(FindAssignmentDto findAssignmentDto) {
        if (findAssignmentDto == null) {
            return assignmentRepository.findAll();
        }
        return assignmentRepositoryCustom.find(findAssignmentDto.getCriteria(), findAssignmentDto.getSort());
    }

    public List<Rule> getAvailableRules() {
        return Arrays.asList(Rule.values());
    }

    public List<PrivilegeEntity> findAll() {
        List<PrivilegeEntity> Available = privilegeRepository.findAll();
        Available.removeIf(item -> (!item.isAvailableForAssignment()));
        return Available;
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
