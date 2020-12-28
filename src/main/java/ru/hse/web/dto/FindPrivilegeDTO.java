package ru.hse.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hse.web.model.Rule;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindPrivilegeDTO {

    private FCriteria criteria;
    private Sort sort;

    @Data
    public static class FCriteria {
        private List<BigInteger> ids;
        private String legalMinistry;
        private Set<Rule> grades;
    }
}
