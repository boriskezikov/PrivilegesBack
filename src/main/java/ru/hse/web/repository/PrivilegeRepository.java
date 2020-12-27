package ru.hse.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hse.web.domain.PrivilegeEntity;

import java.math.BigInteger;

@Repository
public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, BigInteger> {

}
