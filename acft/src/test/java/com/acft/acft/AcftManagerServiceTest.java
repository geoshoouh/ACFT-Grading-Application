package com.acft.acft;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
public class AcftManagerServiceTest {

    @Autowired
    AcftManagerService acftManagerService;

    @Test
    void createNewTestGroupShouldReturnId(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        Assert.notNull(testGroupId, "createNewTestGroup returned null");
    }

    @Test
    void getTestGroupShouldReturnTestGroup(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId);
        Assert.isTrue(testGroup.getId() == testGroupId, "getTestGroup returned the incorrect group");
    }

    @Test
    void createNewSoldierShouldReturnId(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId);
        Long soldierId = acftManagerService.createNewSoldier(testGroup, "Tate", "Joshua", 26, true);
        Assert.notNull(soldierId, "createNewSoldier returned null ID");
    }

    @Test 
    void getSoldierByIdShouldReturnSoldier(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId);
        Long soldierId = acftManagerService.createNewSoldier(testGroup, "Tate", "Joshua", 26, true);
        Soldier soldier = acftManagerService.getSoldierById(soldierId);
        Assert.isTrue(soldier.getId() == soldierId, "getSoldierById returned the incorrect Soldier");
    }

    @Test 
    void getSoldiersByLastNameAndTestGroupIdShouldReturnListOfSoldiers(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId);
        int n = 5;
        String[] lastNames = {"Smith", "Jones", "Samuels", "Smith", "Conway"};
        String[] firstNames = {"Jeff", "Timothy", "Darnell", "Fredrick", "Katherine"};
        int[] ages = {26, 18, 19, 30, 23};
        boolean[] genders = {true, true, true, true, false};
        for (int i = 0; i < n; i++){
            acftManagerService.createNewSoldier(testGroup, lastNames[i], firstNames[i], ages[i], genders[i]);
        }
        List<Soldier> queryResult = acftManagerService.getSoldiersByLastNameAndTestGroupId("Smith", testGroupId);
        Assert.isTrue(queryResult.size() == 2, "error in retrieval of soldiers by lastName and groupId");
    }

    @Test
    void getAllTestGroupsShouldReturnAllExistingTestGroupIds(){
        int n = 5;
        for (int i = 0; i < n; i++) acftManagerService.createNewTestGroup();
        List<Long> allExistingTestGroupIds = acftManagerService.getAllTestGroups();
        Assert.isTrue(allExistingTestGroupIds.size() == n, "getAllTestGroups returned array of unexpected size");
    }

    @Test
    void getSoldiersByTestGroupIdShouldReturnListOfSoldiersWithPassedId(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId);
        int n = 5;
        String[] lastNames = {"Smith", "Jones", "Samuels", "Smith", "Conway"};
        String[] firstNames = {"Jeff", "Timothy", "Darnell", "Fredrick", "Katherine"};
        int[] ages = {26, 18, 19, 30, 23};
        boolean[] genders = {true, true, true, true, false};
        for (int i = 0; i < n; i++){
            acftManagerService.createNewSoldier(testGroup, lastNames[i], firstNames[i], ages[i], genders[i]);
        }
        List<Soldier> soldiersWithCertainGroupId = acftManagerService.getSoldiersByTestGroupId(testGroupId);
        Assert.isTrue(soldiersWithCertainGroupId.size() == n, "getSoldiersByTestGroupId returned array of unexpected size");
    }
}
