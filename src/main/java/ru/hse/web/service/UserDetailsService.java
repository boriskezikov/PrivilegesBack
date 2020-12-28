package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import ru.hse.web.domain.UserDetailsEntity;
import ru.hse.web.dto.AssignPrivilegeDto;
import ru.hse.web.dto.CreateUserInstanceDto;
import ru.hse.web.dto.FactorDto;
import ru.hse.web.dto.PrincipalDto;
import ru.hse.web.repository.PrivilegeRepository;
import ru.hse.web.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static java.lang.String.format;
import static ru.hse.web.service.Utils.buildFullName;
import static ru.hse.web.service.Utils.generateFactorCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsService {

    private final UserRepository userRepository;
    private final PrivilegeRepository privilegeRepository;
    private final SMTPService smtp;

    @Transactional
    public UserDetailsEntity createInactiveUser(CreateUserInstanceDto createUserInstanceDto) {
        String userPassport = createUserInstanceDto.getPassport();
        var exists = userRepository.existsByPassport(userPassport);
        if (exists) {
            throw new IllegalStateException(format("User with passport №%s already exists", userPassport));
        }
        var factorCode = generateFactorCode();
        UserDetailsEntity inactiveUser = UserDetailsEntity.builder()
                .firstName(createUserInstanceDto.getFirstName())
                .middleName(createUserInstanceDto.getMiddleName())
                .lastName(createUserInstanceDto.getLastName())
                .passport(createUserInstanceDto.getPassport())
                .grades(createUserInstanceDto.getCategories())
                .primaryEmail(createUserInstanceDto.getMail())
                .password(createUserInstanceDto.getPassword())
                .factor(factorCode)
                .build();
        inactiveUser = userRepository.save(inactiveUser);
        log.info("Anonymous user {} has been created", inactiveUser.getId());
        smtp.sendSecurityCodeNotification(inactiveUser.getPrimaryEmail(), factorCode, buildFullName(inactiveUser.getFirstName(), inactiveUser.getLastName()));
        return inactiveUser;
    }

    public void activateUser(FactorDto factorDto) {
        userRepository.findById(factorDto.getUserId()).ifPresentOrElse(userDetails -> {
            if (userDetails.getFactor().equals(factorDto.getFactor())) {
                userDetails.setActive(true);
                userRepository.save(userDetails);
                log.info("User {} has been activated", factorDto.getUserId());
                smtp.sendAccountActivatedNotification(userDetails.getPrimaryEmail(), buildFullName(userDetails.getFirstName(), userDetails.getLastName()));
            } else {
                throw new IllegalArgumentException("Codes does not match!");
            }
        }, () -> {
            throw new EntityNotFoundException(format("User %s not found", factorDto.getUserId()));
        });
    }

    public UserDetailsEntity assignPrivilege(@RequestBody AssignPrivilegeDto assignPrivilegeDto) {
        var assignment = privilegeRepository.findById(assignPrivilegeDto.getPrivilegeId()).orElseThrow(EntityNotFoundException::new);
        var user = userRepository.findById(assignPrivilegeDto.getUserId()).orElseThrow(EntityNotFoundException::new);
        var userRules = user.getGrades();
        var assignmentRules = assignment.getGradesRequired();
        userRules.retainAll(assignmentRules);
        if (assignment.isAvailableForAssignment() && userRules.size() > 0) {
            user.getPrivileges().add(assignment);
            var userDetails = userRepository.save(user);
            log.info("Privilege {} assigned to user {}", assignPrivilegeDto.getPrivilegeId(), assignPrivilegeDto.getUserId());
            smtp.sendPrivilegeAssignedNotification(userDetails.getPrimaryEmail(), assignment, buildFullName(userDetails.getFirstName(), userDetails.getLastName()));
            return userDetails;
        }
        if (!assignment.isAvailableForAssignment()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    format("Privilege %s is not available for assignment",
                            assignPrivilegeDto.getPrivilegeId()));
        } else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    format("User %s doesn't have enough grades to assign privilege %s",
                            assignPrivilegeDto.getUserId(), assignPrivilegeDto.getPrivilegeId()));
        }
    }

    public UserDetailsEntity login(PrincipalDto principal) {
        var ud = userRepository.findByPrimaryEmailAndPassword(principal.getUsername(), principal.getPassword())
                .orElseThrow(EntityNotFoundException::new);
        if (!ud.isActive()) {
            throw new IllegalStateException(format("Account %s is not active! Activate account and login again.", ud.getId()));
        }
        smtp.sendLoginActionNotification(ud.getPrimaryEmail(), buildFullName(ud.getFirstName(), ud.getLastName()));
        return ud;
    }

    public List<UserDetailsEntity> findAll() {
        return userRepository.findAll();
    }
}
