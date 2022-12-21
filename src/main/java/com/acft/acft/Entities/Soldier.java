package com.acft.acft.Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;




@Entity
@SequenceGenerator(name="SOLDIER_SEQ", sequenceName = "soldier_sequence")
public class Soldier {

    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOLDIER_SEQ")
    private Long id;

    private Long pseudoId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "TESTGROUP")
    private TestGroup testGroup;


    private String lastName;


    private String firstName;

    private int age;


    private boolean isMale;

    //These are scores
    private int maxDeadlift = 0;

    private int maxDeadliftRaw = 0;

    private int standingPowerThrow = 0;

    private int standingPowerThrowRaw = 0;

    private int handReleasePushups = 0;

    private int handReleasePushupsRaw = 0;

    private int sprintDragCarry = 0;

    private int sprintDragCarryRaw = 0;

    private int plank = 0;

    private int plankRaw = 0;

    private int twoMileRun = 0;

    private int twoMileRunRaw = 0;

    private int totalScore = 0;

    public Soldier(TestGroup testGroup, String lastName, String firstName, int age, boolean isMale) {
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        this.testGroup = testGroup;
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
        this.isMale = isMale;
    }

    protected Soldier(){}
    
    public Long getId() {
        return id;
    }

    public void setPseudoId(Long pseudoId){
        this.pseudoId = pseudoId;
    }

    public Long getPseudoId(){
        return this.pseudoId;
    }

    public TestGroup getTestGroup() {
        return testGroup;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean isMale) {
        this.isMale = isMale;
    }

    @Override
    public String toString() {
        return "Soldier [age=" + age + ", firstName=" + firstName + ", groupId=" + testGroup.getId() + ", id=" + id + ", isMale="
                + isMale + ", lastName=" + lastName + "]";
    }


    public void setId(Long id) {
        this.id = id;
    }
    

    public void setMaxDeadlift(int maxDeadlift) {
        this.maxDeadlift = maxDeadlift;
        this.totalScore += maxDeadlift;
    }


    public void setStandingPowerThrow(int standingPowerThrow) {
        this.standingPowerThrow = standingPowerThrow;
        this.totalScore += standingPowerThrow;
    }


    public void setHandReleasePushups(int handReleasePushups) {
        this.handReleasePushups = handReleasePushups;
        this.totalScore += handReleasePushups;
    }


    public void setSprintDragCarry(int sprintDragCarry) {
        this.sprintDragCarry = sprintDragCarry;
        this.totalScore += sprintDragCarry;
    }


    public void setPlank(int plank) {
        this.plank = plank;
        this.totalScore += plank;
    }


    public void setTwoMileRun(int twoMileRun) {
        this.twoMileRun = twoMileRun;
        this.totalScore += twoMileRun;
    }


    public int getMaxDeadlift() {
        return maxDeadlift;
    }


    public int getMaxDeadliftRaw() {
        return maxDeadliftRaw;
    }


    public void setMaxDeadliftRaw(int maxDeadliftRaw) {
        this.maxDeadliftRaw = maxDeadliftRaw;
    }


    public int getStandingPowerThrow() {
        return standingPowerThrow;
    }


    public int getStandingPowerThrowRaw() {
        return standingPowerThrowRaw;
    }


    public void setStandingPowerThrowRaw(int standingPowerThrowRaw) {
        this.standingPowerThrowRaw = standingPowerThrowRaw;
    }


    public int getHandReleasePushups() {
        return handReleasePushups;
    }


    public int getHandReleasePushupsRaw() {
        return handReleasePushupsRaw;
    }


    public void setHandReleasePushupsRaw(int handReleasePushupsRaw) {
        this.handReleasePushupsRaw = handReleasePushupsRaw;
    }


    public int getSprintDragCarry() {
        return sprintDragCarry;
    }


    public int getSprintDragCarryRaw() {
        return sprintDragCarryRaw;
    }


    public void setSprintDragCarryRaw(int sprintDragCarryRaw) {
        this.sprintDragCarryRaw = sprintDragCarryRaw;
    }


    public int getPlank() {
        return plank;
    }


    public int getPlankRaw() {
        return plankRaw;
    }


    public void setPlankRaw(int plank_raw) {
        this.plankRaw = plank_raw;
    }


    public int getTwoMileRun() {
        return twoMileRun;
    }


    public int getTwoMileRunRaw() {
        return twoMileRunRaw;
    }

    
    public void setTwoMileRunRaw(int twoMileRunRaw) {
        this.twoMileRunRaw = twoMileRunRaw;
    }

    public int getTotalScore(){
        return this.totalScore;
    }

    public int[] getScoresAsArray(boolean raw){
        int[] array = new int[6];
        array[0] = (raw) ? maxDeadliftRaw : maxDeadlift;
        array[1] = (raw) ? standingPowerThrowRaw : standingPowerThrow;
        array[2] = (raw) ? handReleasePushupsRaw : handReleasePushups;
        array[3] = (raw) ? sprintDragCarryRaw : sprintDragCarry;
        array[4] = (raw) ? plankRaw : plank;
        array[5] = (raw) ? twoMileRunRaw : twoMileRun;
        return array;
    }

    public String getRawScoreAsString(int eventId){
        String result;
        switch (eventId){
            case 0:
                result = Integer.toString(maxDeadliftRaw);
                break;
            case 1:
                result = Integer.toString(standingPowerThrowRaw / 10) + "." + standingPowerThrowRaw % 10;
                break;
            case 2:
                result = Integer.toString(handReleasePushupsRaw);
                break;
            case 3:
                String minutes = Integer.toString(sprintDragCarryRaw / 60);
                String seconds = Integer.toString(sprintDragCarryRaw % 60);
                if (seconds.length() < 2) seconds = "0" + seconds;
                result =  minutes + ":" + seconds;
                break;
            case 4:
                minutes = Integer.toString(plankRaw / 60);
                seconds = Integer.toString(plankRaw % 60);
                if (seconds.length() < 2) seconds = "0" + seconds;
                result =  minutes + ":" + seconds;
                break;
            case 5:
                minutes = Integer.toString(twoMileRunRaw / 60);
                seconds = Integer.toString(twoMileRunRaw % 60);
                if (seconds.length() < 2) seconds = "0" + seconds;
                result =  minutes + ":" + seconds;
                break;
            default:
                result = "";
        }
        return result;
    }

    public int getScoreByEventId(int eventId, boolean raw){
        int score = 0;
        switch(eventId){
            case 0:
                score = (raw) ? maxDeadliftRaw : maxDeadlift;
                break;
            case 1:
                score = (raw) ? standingPowerThrowRaw : standingPowerThrow;
                break;
            case 2:
                score = (raw) ? handReleasePushupsRaw : handReleasePushups;
                break;
            case 3:
                score = (raw) ? sprintDragCarryRaw : sprintDragCarry;
                break;
            case 4:
                score = (raw) ? plankRaw : plank;
                break;
            case 5:
                score = (raw) ? twoMileRunRaw : twoMileRun;
                break;
            default: break;
        }
        return score;
    }

}
