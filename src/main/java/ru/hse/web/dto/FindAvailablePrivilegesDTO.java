package ru.hse.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import ru.hse.web.model.Rule;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindAvailablePrivilegesDTO {

    @NotEmpty
    @NonNull
    private Set<Rule> grades;
}
