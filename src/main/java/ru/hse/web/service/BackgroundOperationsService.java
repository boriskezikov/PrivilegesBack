package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Scheduled(cron = "0 0/5 * * * *")
    public void deleteUnsignedUser() {
        log.info("Background cleaner started");
        var now = LocalDateTime.now();
        var toDelete = new ArrayList<UserDetailsEntity>();
        userRepository.findAll().stream().filter(user -> !user.isActive()).forEach(user -> {
            if (Duration.between(user.getTimeCreated(), now).toMinutes() >= 5) {
                toDelete.add(user);
                log.info("Record {} added to remove list", user.getId());
            }
        });
        userRepository.deleteAll(toDelete);
        log.info("Background cleaner finished");
    }
}
