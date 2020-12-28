package ru.hse.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.web.domain.AssignmentEntity;
import ru.hse.web.dto.CreateAssignmentDto;
import ru.hse.web.dto.FindAssignmentDto;
import ru.hse.web.dto.MoveAssignmentDto;
import ru.hse.web.service.PrivilegeService;

import java.util.List;

import static ru.hse.web.Constants.ADMIN;
import static ru.hse.web.Constants.CLIENT;
@Tag(name = "AssignmentController", description = "Allows to operate with assignments")
@RestController
@RequestMapping("/api/v1/privilege/assignment")
@RequiredArgsConstructor
public class AssignmentController {

    private final PrivilegeService privilegeService;

    @Operation(summary = "Move assignment to next stage")
    @PostMapping("/move")
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public AssignmentEntity moveAssignment(@RequestBody MoveAssignmentDto moveAssignmentDto) {
        return privilegeService.moveAssignment(moveAssignmentDto);
    }

    @Operation(summary = "Create new assignment")
    @PostMapping
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public AssignmentEntity createAssignment(@RequestBody CreateAssignmentDto createAssignmentDto) {
        return privilegeService.createAssignment(createAssignmentDto);
    }

    @Operation(summary = "Find assignments")
    @GetMapping
    @Parameter(in = ParameterIn.HEADER, name = "X_GRANT_ID", required = true, schema = @Schema(type = "string", allowableValues = {ADMIN, CLIENT}))
    public List<AssignmentEntity> find(@RequestBody(required = false) FindAssignmentDto findAssignmentDto){
        return privilegeService.findAssignment(findAssignmentDto);
    }


}
