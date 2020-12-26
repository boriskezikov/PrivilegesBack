package ru.hse.web.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.dto.PrivilegeDto;
import ru.hse.web.model.Rule;
import ru.hse.web.service.PrivilegeService;

import java.util.List;

@OpenAPIDefinition(info = @Info(title = "Custom database connector",
        description = "Service for managing database connections",
        version = "1.0",
        contact = @Contact(name = "Kezikov Boris", email = "boris200898@icloud.com")))
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "201", description = "Entity created"),
        @ApiResponse(responseCode = "500", description = "Internal error")
})
@Tag(name = "PrivilegeController", description = "Access to operations with privileges objects")
@RestController
@RequestMapping("/api/v1/privilege")
@RequiredArgsConstructor
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    @Operation(summary = "Create new privilege details instance.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrivilegeEntity createPrivilege(@RequestBody PrivilegeDto dto) {
        return privilegeService.createPrivilege(dto);
    }
    @Operation(summary = "Update privilege details instance.")
    @PutMapping
    public PrivilegeEntity updatePrivilege(@RequestBody PrivilegeDto dto) {
        return privilegeService.updatePrivilege(dto);
    }

    @Operation(summary = "Get all privilege details instance.")
    @GetMapping
    public List<PrivilegeEntity> findAll() {
        return privilegeService.findAll();
    }

    @Operation(summary = "Get all rules for rewards.")
    @GetMapping("/rules")
    public List<Rule> getAvailableRules(){
        return privilegeService.getAvailableRules();
    }

}
