package com.acft.acft;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acft.acft.Exceptions.SoldierNotFoundException;
import com.acft.acft.Exceptions.TestGroupNotFoundException;


@Service
public class AcftManagerService {
    
    @Autowired
    private TestGroupRepository testGroupRepository;

    @Autowired
    private SoldierRepository soldierRepository;

    @Autowired
    private AcftDataConversion acftDataConversion;

    public Long createNewTestGroup(){
        TestGroup testGroup = new TestGroup();
        testGroupRepository.save(testGroup);
        return testGroup.getId();
    }

    public TestGroup getTestGroup(Long testGroupId){
        return testGroupRepository.findById(testGroupId)
            .orElseThrow(() -> new TestGroupNotFoundException(testGroupId));
    }

    public Long createNewSoldier(TestGroup testGroup, String lastName, String firstName, int age, boolean isMale){
        Soldier soldier = new Soldier(testGroup, lastName, firstName, age, isMale);
        soldierRepository.save(soldier);
        return soldier.getId();
    }

    public Soldier getSoldierById(Long soldierId){
        return soldierRepository.findById(soldierId)
            .orElseThrow(() -> new SoldierNotFoundException(soldierId));
    }

    public List<Soldier> getSoldiersByLastNameAndTestGroupId(String lastName, Long testGroupId){
        return soldierRepository.findByLastNameAndTestGroupId(lastName, testGroupId);
    }

    public List<Soldier> getSoldiersByTestGroupId(Long testGroupId){
        return soldierRepository.findByTestGroupId(testGroupId);
    }

    public List<Long> getAllTestGroups(){
        List<TestGroup> allTestGroups =  testGroupRepository.findAll();
        List<Long> allTestGroupIds = new ArrayList<>();
        for (TestGroup testGroup : allTestGroups){
            allTestGroupIds.add(testGroup.getId());
        }
        return allTestGroupIds;
    }

    public int updateSoldierScore(Long soldierId, int eventId, int rawScore){
        Soldier soldier = getSoldierById(soldierId);
        int scaledScore = acftDataConversion.getScore(eventId, rawScore, soldier.isMale(), soldier.getAge());
        switch (eventId){
            case 0:
                soldier.setMaxDeadliftRaw(rawScore);
                soldier.setMaxDeadlift(scaledScore);
                break;
            case 1:
                soldier.setStandingPowerThrowRaw(rawScore);
                soldier.setStandingPowerThrow(scaledScore);
                break;
            case 2:
                soldier.setHandReleasePushupsRaw(rawScore);
                soldier.setHandReleasePushups(scaledScore);
                break;
            case 3:
                soldier.setSprintDragCarryRaw(rawScore);
                soldier.setSprintDragCarry(scaledScore);
                break;
            case 4:
                soldier.setPlankRaw(rawScore);
                soldier.setPlank(scaledScore);
                break;
            case 5:
                soldier.setTwoMileRunRaw(rawScore);
                soldier.setTwoMileRun(scaledScore);
                break;
            default:
                break;
        }
        return scaledScore;
    }
    
}
