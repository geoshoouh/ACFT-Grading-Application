package com.acft.acft.Entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Entity
@Table
public class TestGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pseudoId;

    @JsonIgnore
    private String passcode = "";

    //Lack of zone in Date expression can cause ambiguity with precise deletion time, but no more than 24 hour discrepancy possible
    //This is the case when server IP is in a different timezone. 
    private Date expirationDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lastName")
    public List<Soldier> soldierPopulation = new ArrayList<>();

    public TestGroup(){
        this.expirationDate = Date.from(Instant.now().plus(2, ChronoUnit.DAYS));
    }

    public TestGroup(String passcode){
        this.passcode = passcode;
        this.expirationDate = Date.from(Instant.now().plus(2, ChronoUnit.DAYS));
    }

    public List<Soldier> getSoldierPopulation(){
        return soldierPopulation;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TestGroup [id=" + id + ", expirationDate=" + expirationDate + ", passcode=" + passcode + ", soldierPopulation=" + soldierPopulation + "]";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPasscode() {
        return passcode;
    }

    public Date getExpirationDate(){
        return expirationDate;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public void setExpirationDate(Date expirationDate){
        this.expirationDate = expirationDate;
    }

    public void setPseudoId(Long pseudoId){
        this.pseudoId = pseudoId;
    }

    public Long getPseudoId(){
        return this.pseudoId;
    }

}
