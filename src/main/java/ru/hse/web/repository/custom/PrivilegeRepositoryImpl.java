package ru.hse.web.repository.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.dto.FindPrivilegeDTO;
import ru.hse.web.dto.PrivilegeDto;
import ru.hse.web.dto.Sort;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.swing.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class PrivilegeRepositoryImpl {

    private final EntityManager entityManager;


    public List<PrivilegeEntity> find(FindPrivilegeDTO.FCriteria criteria, Sort sort) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PrivilegeEntity> query = criteriaBuilder.createQuery(PrivilegeEntity.class);
        Root<PrivilegeEntity> root = query.from(PrivilegeEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        ofNullable(criteria.getIds()).ifPresent(option -> {
            CriteriaBuilder.In<BigInteger> inClause = criteriaBuilder.in(root.get("id"));
            option.forEach(inClause::value);
            predicates.add(inClause);
        });

        ofNullable(criteria.getGrades())
                .ifPresent(option -> option.forEach(member -> {
                    predicates.add(criteriaBuilder.isMember(member, root.get("gradesRequired")));
                }));

        ofNullable(criteria.getLegalMinistry())
                .ifPresent(option -> predicates.add(criteriaBuilder.equal(root.get("legalMinistry"), option)));

        predicates.add(criteriaBuilder.equal(root.get("availableForAssignment"), true));

        ofNullable(sort)
                .ifPresent(sortingStrategy -> {
                    if (Arrays.stream(PrivilegeDto.class.getDeclaredFields())
                            .map(Field::getName)
                            .collect(Collectors.toList()).contains(sort.getSortBy())) {
                        if (sort.getType().equals(SortOrder.ASCENDING)) {
                            query.orderBy(criteriaBuilder.asc(root.get(sort.getSortBy())));
                        } else if (sort.getType().equals(SortOrder.DESCENDING)) {
                            query.orderBy(criteriaBuilder.desc(root.get(sort.getSortBy())));
                        }
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(
                                "Incorrect sorting strategy passed! Object does not contain field %s", sort.getSortBy()));
                    }
                });

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }
}
