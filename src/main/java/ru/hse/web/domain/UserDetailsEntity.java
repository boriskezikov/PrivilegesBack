package ru.hse.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.hse.web.model.Rule;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Entity(name = "user_details")
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_details_ids_gen")
    @SequenceGenerator(name = "user_details_ids_gen", sequenceName = "user_details_id_seq", allocationSize = 1)
    private BigInteger id;

    @Column(nullable = false, length = 25)
    private String firstName;

    @Column(nullable = false, length = 25)
    private String lastName;

    @Column
    private String middleName;

    @JsonIgnore
    @Column(nullable = false, length = 11)
    private String passport;

    @Column(nullable = false, unique = true)
    private String primaryEmail;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PrivilegeEntity> privileges;

    @ElementCollection
    private Set<Rule> grades;

    @JsonIgnore
    @Column
    private boolean isActive;

    @CreationTimestamp
    private LocalDateTime timeCreated;

    @JsonIgnore
    @Column(nullable = false)
    private String factor;
}
