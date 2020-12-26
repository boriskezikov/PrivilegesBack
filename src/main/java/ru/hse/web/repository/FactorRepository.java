package ru.hse.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hse.web.domain.FactorAuthEntity;

import java.math.BigDecimal;
import java.math.BigInteger;

@Repository
public interface FactorRepository extends JpaRepository<FactorAuthEntity, BigInteger> {

    FactorAuthEntity findByUser_Id(BigInteger userId);
}
