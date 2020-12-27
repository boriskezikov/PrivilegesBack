package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.hse.web.domain.UserDetailsEntity;
import ru.hse.web.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgroundOperationsService {

    private final UserRepository userRepository;
    private final SMTPService smtpService;

    @Value("${bg.time.expiration_min}")
    private int expiryTime;

    @Scheduled(cron = "${bg.time.cron}")
    public void deleteUnsignedUser() {
        var now = LocalDateTime.now();
        var toDelete = new ArrayList<UserDetailsEntity>();
        userRepository.findAll().stream().filter(user -> !user.isActive()).forEach(user -> {
            if (Duration.between(user.getTimeCreated(), now).toMinutes() >= expiryTime) {
                toDelete.add(user);
                log.info("Record {} added to remove list", user.getId());
                smtpService.sendAccountExpired(user.getPrimaryEmail(), "client");
            }
        });
        userRepository.deleteAll(toDelete);
    }
}
