package com.acft.acft.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acft.acft.Entities.TestGroup;

import java.util.Date;
import java.util.List;


public interface TestGroupRepository extends JpaRepository<TestGroup, Long>{

    List<TestGroup> findByExpirationDateBefore(Date date);

    TestGroup findByPseudoId(Long pseudoId);
}
