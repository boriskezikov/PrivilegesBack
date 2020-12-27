package ru.hse.web.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.web.domain.UserDetailsEntity;
import ru.hse.web.dto.AssignPrivilegeDto;
import ru.hse.web.dto.CreateUserInstanceDto;
import ru.hse.web.dto.FactorDto;
import ru.hse.web.dto.PrincipalDto;
import ru.hse.web.service.UserDetailsService;

import java.util.List;

@Tag(name = "UserDetailsController", description = "Access to operations with user details objects")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserDetailsController {

    private final UserDetailsService userDetailsService;

    @Operation(summary = "Create new user details instance. Account will be created anonymously, activation in 5 min is required")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsEntity createUserDetails(@RequestBody @Validated CreateUserInstanceDto createUserInstanceDto) {
        return userDetailsService.createInactiveUser(createUserInstanceDto);
    }

    @Operation(summary = "Activate user account with 2fa email code.")
    @PostMapping("/activate")
    public void activate(@RequestBody FactorDto factorDto) {
        userDetailsService.activateUser(factorDto);
    }

    @Operation(summary = "Load all user accounts")
    @GetMapping
    public List<UserDetailsEntity> findAll() {
        return userDetailsService.findAll();
    }

    @Operation(summary = "Assign privilege to specified user")
    @PostMapping("/assign")
    public UserDetailsEntity assignPrivilege(@RequestBody AssignPrivilegeDto assignPrivilegeDto) {
        return userDetailsService.assignPrivilege(assignPrivilegeDto);
    }

    @Operation(summary = "Returns user details by login/password pair")
    @PostMapping("/login")
    public UserDetailsEntity login(@RequestBody PrincipalDto principal){
        return userDetailsService.login(principal);
    }
}
