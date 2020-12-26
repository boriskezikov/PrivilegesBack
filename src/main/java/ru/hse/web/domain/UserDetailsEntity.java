package ru.hse.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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

    @Column(nullable = false, length = 11)
    private String passport;

    @Column(nullable = false)
    private String primaryEmail;

    @Column(nullable = false)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PrivilegeEntity> privileges;

    @ElementCollection
    private Set<Rule> grades;

    @Column
    private boolean isActive;
}
