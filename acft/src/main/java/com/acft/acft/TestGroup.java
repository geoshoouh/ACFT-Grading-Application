package com.acft.acft;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class TestGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "test_group_id")
    private Long id;

    @OneToMany(mappedBy = "testGroup")
    public List<Soldier> soldierPopulation = new ArrayList<>();

    protected TestGroup(){}

    public List<Soldier> getSoldierPopulation(){
        return soldierPopulation;
    }


    public void addSoldier(Soldier soldier){
        soldierPopulation.add(soldier);
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TestGroup [id=" + id + ", soldierPopulation=" + soldierPopulation + "]";
    }

}
