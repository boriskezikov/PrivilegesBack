package ru.hse.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.domain.UserDetailsEntity;
import ru.hse.web.dto.FindAvailablePrivilegesDTO;
import ru.hse.web.dto.FindPrivilegeDTO;
import ru.hse.web.model.Rule;
import ru.hse.web.service.PrivilegeService;
import ru.hse.web.service.UserDetailsService;

import java.math.BigInteger;
import java.util.List;

import static ru.hse.web.Constants.ADMIN;
import static ru.hse.web.Constants.CLIENT;


@Tag(name = "OpenController", description = "All fetching methods")
@RestController
@RequiredArgsConstructor
public class FetchInfoController {

    private final UserDetailsService userDetailsService;
    private final PrivilegeService privilegeService;

    @Operation(summary = "Load all user accounts")
    @GetMapping("/api/v1/open/user")
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<UserDetailsEntity> findAll() {
        return userDetailsService.findAll();
    }

    @Operation(summary = "Filter searching for privileges")
    @GetMapping("/api/v1/open/user/{userId}/available")
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<PrivilegeEntity> loadAvailable(@PathVariable BigInteger userId) {
        return privilegeService.loadAvailable(userId);
    }

    @Operation(summary = "Filter searching for privileges for anon user")
    @GetMapping("/api/v1/open/privilege/available")
    @ResponseBody
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<PrivilegeEntity> loadAvailable(@Validated @RequestBody FindAvailablePrivilegesDTO findAvailablePrivilegesDTO) {
        return privilegeService.loadAvailable(findAvailablePrivilegesDTO.getGrades());
    }

    @Operation(summary = "Filter searching for privileges")
    @GetMapping("/api/v1/open/privilege/find")
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<PrivilegeEntity> find(@RequestBody(required = false) FindPrivilegeDTO findPrivilegeDTO) {
        return privilegeService.findPrivilege(findPrivilegeDTO);
    }

    @Operation(summary = "Get all rules for rewards.")
    @GetMapping("/api/v1/open/privilege/rules")
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<Rule> getAvailableRules() {
        return privilegeService.getAvailableRules();
    }
}
