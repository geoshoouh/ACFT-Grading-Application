package com.acft.acft.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class PseudoId {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "pseudo_id_sequence_generator", sequenceName = "post_id_sequence")
    private Long id;

    private Long pseudoId;
    
    public PseudoId(Long pseudoId){
        this.pseudoId = pseudoId;
    }

    protected PseudoId(){}

    public Long getId(){
        return this.id;
    }

    public Long getPseudoId(){
        return this.pseudoId;
    }
}
