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

    @Column(name = "passcode")
    private String passcode = "";

    @OneToMany(mappedBy = "testGroup")
    public List<Soldier> soldierPopulation = new ArrayList<>();

    public TestGroup(){}

    public TestGroup(String passcode){
        this.passcode = passcode;
    }

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
        return "TestGroup [id=" + id + ", passcode=" + passcode + ", soldierPopulation=" + soldierPopulation + "]";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public void setSoldierPopulation(List<Soldier> soldierPopulation) {
        this.soldierPopulation = soldierPopulation;
    }

}
