package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.domain.UserDetailsEntity;
import ru.hse.web.repository.PrivilegeRepository;
import ru.hse.web.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.hse.web.service.Utils.buildFullName;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgroundOperationsService {

    private final UserRepository userRepository;
    private final PrivilegeRepository privilegeRepository;
    private final SMTPService smtpService;

    @Value("${bg.time.expiration_min}")
    private int expiryTime;

    @Scheduled(cron = "${bg.time.remove-cron}")
    public void deleteUnsignedUser() {
        var now = LocalDateTime.now();
        var toDelete = new ArrayList<UserDetailsEntity>();
        userRepository.findAll().stream().filter(user -> !user.isActive()).forEach(user -> {
            if (Duration.between(user.getTimeCreated(), now).toMinutes() >= expiryTime) {
                toDelete.add(user);
                log.info("Record {} added to remove list", user.getId());
                smtpService.sendAccountExpiredNotification(user.getPrimaryEmail(), "client");
            }
        });
        userRepository.deleteAll(toDelete);
    }

    @Scheduled(cron = "${bg.time.available-cron}")
    public void notifyUsersAboutNewPrivileges() {
        var users = userRepository.findAll();
        var privileges = privilegeRepository.findAll();
        users.forEach(user -> {
            var assignedPrivileges = user.getPrivileges();
            List<PrivilegeEntity> available = privileges.stream()
                    .filter(pr -> !assignedPrivileges.contains(pr))
                    .collect(Collectors.toList());
            if (available.size() > 0) {
                smtpService.sendAvailablePrivilegesNotification(user.getPrimaryEmail(), buildFullName(user.getFirstName(), user.getLastName()), available);
            }
        });
    }
}
