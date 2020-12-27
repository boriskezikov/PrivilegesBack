package ru.hse.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.hse.web.model.Rule;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Entity(name = "privileges")
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "privileges_ids_gen")
    @SequenceGenerator(name = "privileges_ids_gen", sequenceName = "privileges_id_seq", allocationSize = 1)
    private BigInteger id;

    @Column(nullable = false)
    private String legalMinistry;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @CreationTimestamp
    private LocalDateTime timeCreated;

    @Column
    private boolean availableForAssignment;

    /*At least one rule must be present for privilege availability*/
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    @ElementCollection(targetClass = Rule.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "privelege_required_grades", joinColumns = @JoinColumn(name = "privilege_id"))
    private Set<Rule> gradesRequired;

}
