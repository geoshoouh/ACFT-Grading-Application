package com.acft.acft;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


public interface TestGroupRepository extends JpaRepository<TestGroup, Long>{

    List<TestGroup> findByExpirationDateBefore(Date date);
}
