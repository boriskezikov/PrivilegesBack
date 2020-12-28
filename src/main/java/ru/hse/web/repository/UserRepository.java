package ru.hse.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hse.web.domain.UserDetailsEntity;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDetailsEntity, BigInteger> {

    boolean existsByPassport(String passport);

    boolean existsByPrimaryEmail(String primary_email);

    Optional<UserDetailsEntity> findByPrimaryEmailAndPassword(String mail, String pass);
}
