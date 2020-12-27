package ru.hse.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hse.web.domain.AssignmentEntity;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<AssignmentEntity, BigInteger> {

    Optional<AssignmentEntity> findByUser_IdAndPrivilege_Id(BigInteger userId, BigInteger privId);
}
