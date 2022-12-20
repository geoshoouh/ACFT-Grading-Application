package com.acft.acft.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acft.acft.Entities.Soldier;

import java.util.List;

public interface SoldierRepository extends JpaRepository<Soldier, Long>{
    
    List<Soldier> findByTestGroupIdOrderByLastNameAsc(Long testGroupId);

    Soldier findByPseudoId(Long pseudoId);
}
