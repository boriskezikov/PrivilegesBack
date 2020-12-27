package ru.hse.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hse.web.model.AssignmentStatus;
import ru.hse.web.model.Rule;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindAssignmentDto {

    private FindAssignmentDto.SCriteria criteria;
    private Sort sort;

    @Data
    public static class SCriteria {
        private List<BigInteger> assignmentIds;
        private List<BigInteger> usersIds;
        private AssignmentStatus assignmentStatus;
    }
}
