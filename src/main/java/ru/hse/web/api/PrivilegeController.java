package ru.hse.web.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.dto.FindAvailablePrivilegesDTO;
import ru.hse.web.dto.FindPrivilegeDTO;
import ru.hse.web.dto.PrivilegeDto;
import ru.hse.web.model.Rule;
import ru.hse.web.service.PrivilegeService;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static ru.hse.web.Constants.ADMIN;
import static ru.hse.web.Constants.CLIENT;


@OpenAPIDefinition(info = @Info(title = "Privilege provider service",
        description = "Service for managing civil privileges",
        version = "1.0",
        contact = @Contact(name = "Kezikov Boris, Gorelov Evgen", email = "boris200898@icloud.com")))
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful request"),
        @ApiResponse(responseCode = "201", description = "Entity created"),
        @ApiResponse(responseCode = "500", description = "Internal error")
})
@Tag(name = "PrivilegeController", description = "Access to operations with privilege objects")
@RestController
@RequestMapping("/api/v1/privilege")
@RequiredArgsConstructor
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    @Operation(summary = "Create new privilege details instance.")
    @PostMapping
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    @ResponseStatus(HttpStatus.CREATED)
    public PrivilegeEntity createPrivilege(@RequestBody PrivilegeDto dto) {
        return privilegeService.createPrivilege(dto);
    }

    @Operation(summary = "Update privilege details instance.")
    @PutMapping
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public PrivilegeEntity updatePrivilege(@RequestBody PrivilegeDto dto) {
        return privilegeService.updatePrivilege(dto);
    }

    @Operation(summary = "Get all rules for rewards.")
    @GetMapping("/rules")
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<Rule> getAvailableRules() {
        return privilegeService.getAvailableRules();
    }

    @Operation(summary = "Remove privilege record by id")
    @DeleteMapping("/{id}")
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public void remove(@PathVariable BigInteger id) {
        privilegeService.removeById(id);
    }


    @Operation(summary = "Filter searching for privileges")
    @GetMapping("/find")
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<PrivilegeEntity> find(@RequestBody(required = false) FindPrivilegeDTO findPrivilegeDTO) {
        return privilegeService.findPrivilege(findPrivilegeDTO);
    }

    @Operation(summary = "Filter searching for privileges")
    @GetMapping("{userId}/available")
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<PrivilegeEntity> loadAvailable(@PathVariable BigInteger userId) {
        return privilegeService.loadAvailable(userId);
    }

    @Operation(summary = "Filter searching for privileges for anon user")
    @GetMapping("/available")
    @ResponseBody
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<PrivilegeEntity> loadAvailable(@Validated @RequestBody FindAvailablePrivilegesDTO findAvailablePrivilegesDTO) {
        return privilegeService.loadAvailable(findAvailablePrivilegesDTO.getGrades());
    }
}
