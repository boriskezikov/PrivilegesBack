package ru.hse.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hse.web.model.Rule;

import java.math.BigInteger;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeDto {

    private BigInteger id;
    private String legalMinistry;
    private String text;
    private boolean availableForAssignment;
    private Set<Rule> gradesRequired;
}
