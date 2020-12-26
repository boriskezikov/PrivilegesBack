package ru.hse.web.api;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.web.domain.UserDetailsEntity;
import ru.hse.web.dto.AssignPrivilegeDto;
import ru.hse.web.dto.CreateUserInstanceDto;
import ru.hse.web.dto.FactorDto;
import ru.hse.web.service.UserDetailsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserDetailsController {

    private final UserDetailsService userDetailsService;

    @PostMapping("/create")
    public UserDetailsEntity createUserDetails(@RequestBody @Validated CreateUserInstanceDto createUserInstanceDto) {
        return userDetailsService.createInactiveUser(createUserInstanceDto);
    }

    @PostMapping("/activate")
    public void activate(@RequestBody FactorDto factorDto) {
        userDetailsService.activateUser(factorDto);
    }

    @GetMapping
    public List<UserDetailsEntity> findAll() {
        return userDetailsService.findAll();
    }

    @PostMapping("/assign")
    public UserDetailsEntity assignPrivilege(@RequestBody AssignPrivilegeDto assignPrivilegeDto) {
        return userDetailsService.assignPrivilege(assignPrivilegeDto);
    }
}
