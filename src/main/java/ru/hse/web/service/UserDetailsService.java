package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import ru.hse.web.domain.FactorAuthEntity;
import ru.hse.web.domain.UserDetailsEntity;
import ru.hse.web.dto.AssignPrivilegeDto;
import ru.hse.web.dto.CreateUserInstanceDto;
import ru.hse.web.dto.FactorDto;
import ru.hse.web.repository.FactorRepository;
import ru.hse.web.repository.PrivilegeRepository;
import ru.hse.web.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Random;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsService {

    private final UserRepository userRepository;
    private final FactorRepository factorRepository;
    private final PrivilegeRepository privilegeRepository;
    private final SMTPService smtp;


    public UserDetailsEntity createInactiveUser(CreateUserInstanceDto createUserInstanceDto) {
        String userPassport = createUserInstanceDto.getPassport();
        var exists = userRepository.existsByPassport(userPassport);
        if (exists) {
            throw new IllegalStateException(format("User with passport №%s already exists", userPassport));
        }
        UserDetailsEntity inactiveUser = UserDetailsEntity.builder()
                .firstName(createUserInstanceDto.getFirstName())
                .middleName(createUserInstanceDto.getMiddleName())
                .lastName(createUserInstanceDto.getLastName())
                .passport(createUserInstanceDto.getPassport())
                .grades(createUserInstanceDto.getCategories())
                .primaryEmail(createUserInstanceDto.getMail())
                .password(createUserInstanceDto.getPassword())
                .build();
        inactiveUser = userRepository.save(inactiveUser);
        log.info("Anonymous user {} has been created", inactiveUser.getId());
        var factorCode = generateFactorCode();
        factorRepository.save(FactorAuthEntity.builder().user(inactiveUser).factorCode(factorCode).build());
        smtp.sendSecurityCode(inactiveUser.getPrimaryEmail(), factorCode);
        return inactiveUser;
    }

    public void activateUser(FactorDto factorDto) {
        userRepository.findById(factorDto.getUserId()).ifPresentOrElse(userDetails -> {
            FactorAuthEntity factorAuthEntity = factorRepository.findByUser_Id(factorDto.getUserId());
            if (factorAuthEntity.getFactorCode().equals(factorDto.getFactor())) {
                userDetails.setActive(true);
                userRepository.save(userDetails);
                log.info("User {} has been activated", factorDto.getUserId());
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
        if (userRules.size() > 0) {
            user.getPrivileges().add(assignment);
            var userDetails = userRepository.save(user);
            log.info("Privilege {} assigned to user {}", assignPrivilegeDto.getPrivilegeId(), assignPrivilegeDto.getUserId());
            return userDetails;
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                format("User %s doesn't have enough grades to assign privilege %s",
                        assignPrivilegeDto.getUserId(), assignPrivilegeDto.getPrivilegeId()));
    }


    public List<UserDetailsEntity> findAll() {
        return userRepository.findAll();
    }

    private String generateFactorCode() {
        return String.valueOf(new Random().nextInt(9995));
    }

}
