package com.acft.acft;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoldierRepository extends JpaRepository<Soldier, Long>{
    
    List<Soldier> findByLastNameAndTestGroupId(String lastName, Long testGroupId);
    
    List<Soldier> findByTestGroupId(Long testGroupId);
}
