package com.acft.acft;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.acft.acft.Entities.Soldier;
import com.acft.acft.Entities.TestGroup;
import com.acft.acft.Services.AcftManagerService;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
    void createNewTestGroupWithPasscodeShouldReturnId(){
        String passcode = "password";
        Long testGroupId = acftManagerService.createNewTestGroup(passcode);
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, passcode);
        Assert.notNull(testGroupId, "createNewTestGroup returned null");
        Assert.isTrue(testGroup.getPasscode().equals(passcode), "createNewTestGroup w/passcode failed to create instance with expected passcode attribute");
    }

    @Test
    void getTestGroupShouldReturnTestGroup(){
        //case w/passode
        String passcode = "password";
        Long testGroupId = acftManagerService.createNewTestGroup(passcode);
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, passcode);
        Assert.isTrue(testGroup.getId() == testGroupId, "getTestGroup returned the incorrect group");
        //case w/o passcode
        Long testGroupIdEmptyPasscode = acftManagerService.createNewTestGroup();
        TestGroup testGroupEmptyPasscode = acftManagerService.getTestGroup(testGroupIdEmptyPasscode, "");
        Assert.isTrue(testGroupEmptyPasscode.getId() == testGroupIdEmptyPasscode, "getTestGroup returned the incorrect group");
    }

    @Test
    void createNewSoldierShouldReturnId(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate", "Joshua", 26, true);
        Assert.notNull(soldierId, "createNewSoldier returned null ID");
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == 1, "In createNewTestGroupShouldReturnId: unexpected soldierPopulation value after solider addition");
    }

    @Test 
    void getSoldierByIdShouldReturnSoldier(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate", "Joshua", 26, true);
        Soldier soldier = acftManagerService.getSoldierById(soldierId);
        Assert.isTrue(soldier.getId() == soldierId, "getSoldierById returned the incorrect Soldier");
    }

    //Tests like this passed when run alone, but failed when all methods in the class are executed
    //Added initial query to establish reference
    //This is perhaps a lazy solution to mitigate a lack of familiarity with how Spring Boot tests execute
    @Test
    void getAllTestGroupsShouldReturnAllExistingTestGroupIds(){
        int reference = acftManagerService.getAllTestGroups().size();
        int n = 5;
        for (int i = 0; i < n; i++) acftManagerService.createNewTestGroup();
        List<Long> allExistingTestGroupIds = acftManagerService.getAllTestGroups();
        Assert.isTrue(allExistingTestGroupIds.size() == reference + n, "getAllTestGroups returned array of unexpected size");
    }

    @Test
    void getSoldiersByTestGroupIdShouldReturnListOfSoldiersWithPassedId(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        int n = 5;
        String[] lastNames = {"Smith", "Jones", "Samuels", "Smith", "Conway"};
        String[] firstNames = {"Jeff", "Timothy", "Darnell", "Fredrick", "Katherine"};
        int[] ages = {26, 18, 19, 30, 23};
        boolean[] genders = {true, true, true, true, false};
        for (int i = 0; i < n; i++){
            acftManagerService.createNewSoldier(testGroupId, lastNames[i], firstNames[i], ages[i], genders[i]);
        }
        List<Soldier> soldiersWithCertainGroupId = acftManagerService.getSoldiersByTestGroupId(testGroupId, "");
        Assert.isTrue(soldiersWithCertainGroupId.size() == n, "getSoldiersByTestGroupId returned array of unexpected size");
    }

    @Test
    void updateSoldierScoreShouldReturnCorrectScaledScore(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate" , "Joshua", 31, true);
        acftManagerService.updateSoldierScore(soldierId, 1, 110, "");
        //Expected conversion for 31 year old male scoring 110 cm on the standing power throw is 90 points
        int expectedScore = 89;
        int convertedScore = acftManagerService.getSoldierById(soldierId).getStandingPowerThrow();
        Assert.isTrue(convertedScore == expectedScore, "expected score was " + expectedScore + " and actual score was " + convertedScore);
        acftManagerService.updateSoldierScore(soldierId, 2, 20, "gnar");
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, "");
        testGroup.setPasscode("password");
        int expectedScore2 = 75;
        int convertedScore2 = acftManagerService.updateSoldierScore(soldierId, 3, 120, "");
        Assert.isTrue(convertedScore2 == expectedScore2, "expected score was " + expectedScore2 + " and actual score was " + convertedScore2);
    }

    @Test
    @Transactional
    void deleteTestGroupsOnScheduleDeletesExpiredTestGroups(){
        int reference = acftManagerService.getAllTestGroups().size();
        Long testGroupId1 = acftManagerService.createNewTestGroup();
        Long testGroupId2 = acftManagerService.createNewTestGroup();
        TestGroup testGroup1 = acftManagerService.getTestGroup(testGroupId1, "");
        TestGroup testGroup2 = acftManagerService.getTestGroup(testGroupId2, "");
        System.out.println("before changes: " + testGroup1);
        testGroup1.setExpirationDate(Date.from(Instant.now().minus(5, ChronoUnit.DAYS)));
        testGroup2.setExpirationDate(Date.from(Instant.now().plus(5, ChronoUnit.DAYS)));
        acftManagerService.deleteTestGroupsOnSchedule();
        Assert.isTrue(acftManagerService.getAllTestGroups().size() == reference + 1, "deleteTestGroupsOnSchedule did not produce expected results. Expected repo size " + 1 + ", size was actually " + acftManagerService.getAllTestGroups().size());
    }

    @Test
    void getXlsxFileForTestGroupDataGetsExpectedFile(){
        int size = 5;
        Long testGroupId = acftManagerService.populateDatabase(size);
        File file = acftManagerService.getXlsxFileForTestGroupData(testGroupId, "");
        Assert.isTrue(file.getName().equals("testGroup_" + testGroupId + ".xlsx"), "In getXlsxFileForTestGroupDataGetsExpectedFile: File not found");
        file.delete();
    }

    @Test
    void flushDatabaseDeletesAllEntities(){
        int size = 5;
        acftManagerService.populateDatabase(size);
        Assert.isTrue(acftManagerService.getSoldierRepositorySize() > 0 && acftManagerService.getTestGroupRepositorySize() > 0, "In flushDatabseDeletesAllEntities: database population failed");
        acftManagerService.flushDatabase();
        Assert.isTrue(acftManagerService.getSoldierRepositorySize() == 0 && acftManagerService.getTestGroupRepositorySize() == 0, "In flushDatabseDeletesAllEntities: flushDatabase() failed");
    }

    @Test
    @Transactional
    void deleteSoldiersByIdPersistsDeletion(){
        String passcode = "";
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate", "Joshua", 26, true);
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == 1, "In deleteSoldiersByIdPersistsDeletion: unexpected TestGroup population size after soldier instantiation");
        acftManagerService.deleteSoldierById(testGroupId, passcode, soldierId);
        System.out.println("pop size: " + acftManagerService.getTestGroup(testGroupId, passcode).getSoldierPopulation().size());
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == 0, "In deleteSoldiersByIdPersistsDeletion: unexpected TestGroup population size " + acftManagerService.getTestGroup(testGroupId, passcode).getSoldierPopulation().size() + " after soldier deletion");
    }

}
