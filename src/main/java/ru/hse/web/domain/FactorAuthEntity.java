package ru.hse.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.math.BigInteger;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Entity(name = "factors")
@NoArgsConstructor
@AllArgsConstructor
public class FactorAuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "factors_ids_gen")
    @SequenceGenerator(name = "factors_ids_gen", sequenceName = "factors_id_seq", allocationSize = 1)
    private BigInteger id;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_details_id", nullable = false)
    private UserDetailsEntity user;

    private String factorCode;
}
