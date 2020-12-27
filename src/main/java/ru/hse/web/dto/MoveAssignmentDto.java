package ru.hse.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hse.web.model.AssignmentStatus;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoveAssignmentDto {

    private BigInteger assignmentId;
    private AssignmentStatus status;

}
