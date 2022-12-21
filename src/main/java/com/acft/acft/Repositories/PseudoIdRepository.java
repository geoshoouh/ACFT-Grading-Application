package com.acft.acft.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acft.acft.Entities.PseudoId;

import java.util.List;

public interface PseudoIdRepository extends JpaRepository<PseudoId, Long> {
    
    PseudoId findFirstByOrderByPseudoId();

    List<PseudoId> findByPseudoId(Long PseudoId);

}
