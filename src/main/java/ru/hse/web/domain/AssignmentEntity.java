package ru.hse.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.hse.web.model.AssignmentStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Entity(name = "assignments")
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assignments_ids_gen")
    @SequenceGenerator(name = "assignments_ids_gen", sequenceName = "assignments_id_seq", allocationSize = 1)
    private BigInteger id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserDetailsEntity user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PrivilegeEntity privilege;

    @Builder.Default
    @Column
    @Enumerated(value = EnumType.STRING)
    private AssignmentStatus assignmentStatus = AssignmentStatus.OPEN;

    @CreationTimestamp
    private LocalDateTime timeCreated;
}
