package ru.hse.web.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.dto.PrivilegeDto;
import ru.hse.web.model.Rule;
import ru.hse.web.service.PrivilegeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/privilege")
@RequiredArgsConstructor
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    @PostMapping
    public PrivilegeEntity createPrivilege(@RequestBody PrivilegeDto dto) {
        return privilegeService.createPrivilege(dto);
    }

    @PutMapping
    public PrivilegeEntity updatePrivilege(@RequestBody PrivilegeDto dto) {
        return privilegeService.updatePrivilege(dto);
    }

    @GetMapping
    public List<PrivilegeEntity> findAll() {
        return privilegeService.findAll();
    }

    @GetMapping("/rules")
    public List<Rule> getAvailableRules(){
        return privilegeService.getAvailableRules();
    }

}
