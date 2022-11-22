package com.acft.acft.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;




@Entity
@SequenceGenerator(name="SOLDIER_SEQ", sequenceName = "soldier_sequence")
@Table(name = "SOLDIERS")
public class Soldier {

    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOLDIER_SEQ")
    @Column(name = "soldier_id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "testgroup_id", referencedColumnName = "id")
    private TestGroup testGroup;

    @Column(name = "test_group_id")
    private Long testGroupId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "age")
    private int age;

    @Column(name = "is_male")
    private boolean isMale;

    //These are scores
    @Column(name = "max_deadlift")
    private int maxDeadlift = 0;

    @Column(name = "max_deadlist_raw")
    private int maxDeadliftRaw = 0;

    @Column(name = "standing_power_throw")
    private int standingPowerThrow = 0;

    @Column(name = "standing_power_throw_raw")
    private int standingPowerThrowRaw = 0;

    @Column(name = "hand_release_pushups")
    private int handReleasePushups = 0;

    @Column(name = "hand_realease_pushups_raw")
    private int handReleasePushupsRaw = 0;

    @Column(name = "sprint_drag_carry")
    private int sprintDragCarry = 0;

    @Column(name = "sprint_drag_carry_raw")
    private int sprintDragCarryRaw = 0;

    @Column(name = "plank")
    private int plank = 0;

    @Column(name = "plank_raw")
    private int plankRaw = 0;

    @Column(name = "two_mile_run")
    private int twoMileRun = 0;

    @Column(name = "two_mile_run_raw")
    private int twoMileRunRaw = 0;

    public Soldier(TestGroup testGroup, String lastName, String firstName, int age, boolean isMale) {
        this.testGroup = testGroup;
        this.testGroupId = testGroup.getId();
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
        this.isMale = isMale;
    }

    protected Soldier(){}
    
    public Long getId() {
        return id;
    }

    public TestGroup getTestGroup() {
        return testGroup;
    }

    public Long getTestGroupId() {
        return testGroupId;
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
        return "Soldier [age=" + age + ", firstName=" + firstName + ", groupId=" + testGroupId + ", id=" + id + ", isMale="
                + isMale + ", lastName=" + lastName + "]";
    }


    public void setId(Long id) {
        this.id = id;
    }
    

    public void setMaxDeadlift(int maxDeadlift) {
        this.maxDeadlift = maxDeadlift;
    }


    public void setStandingPowerThrow(int standingPowerThrow) {
        this.standingPowerThrow = standingPowerThrow;
    }


    public void setHandReleasePushups(int handReleasePushups) {
        this.handReleasePushups = handReleasePushups;
    }


    public void setSprintDragCarry(int sprintDragCarry) {
        this.sprintDragCarry = sprintDragCarry;
    }


    public void setPlank(int plank) {
        this.plank = plank;
    }


    public void setTwoMileRun(int twoMileRun) {
        this.twoMileRun = twoMileRun;
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
        return maxDeadlift + standingPowerThrow + handReleasePushups + sprintDragCarry + plank + twoMileRun;
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
