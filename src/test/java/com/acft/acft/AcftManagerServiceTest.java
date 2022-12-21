package com.acft.acft;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.acft.acft.Entities.Soldier;
import com.acft.acft.Entities.TestGroup;
import com.acft.acft.Services.AcftManagerService;
import com.acft.acft.Services.GenerateRandomData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;


@SpringBootTest
public class AcftManagerServiceTest {

    @Autowired
    AcftManagerService acftManagerService;

    GenerateRandomData generateRandomData = new GenerateRandomData();

    BulkSoldierUploadTest bulkSoldierUploadTest = new BulkSoldierUploadTest();

    String testPath = "src/main/resources/data/bulkUploadTest.xlsx";

    @Test
    void createNewTestGroupShouldReturnId() throws Exception{
        Long testGroupId = acftManagerService.createNewTestGroup();
        Assert.notNull(testGroupId, "createNewTestGroup returned null");
    }

    @Test
    void createNewTestGroupWithPasscodeShouldReturnId() throws Exception{
        String passcode = "password";
        Long testGroupId = acftManagerService.createNewTestGroup(passcode);
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, passcode);
        Assert.notNull(testGroupId, "createNewTestGroup returned null");
        Assert.isTrue(testGroup.getPasscode().equals(passcode), "createNewTestGroup w/passcode failed to create instance with expected passcode attribute");
    }

    @Test
    void getTestGroupShouldReturnTestGroup() throws Exception{
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
    void createNewSoldierShouldReturnId() throws Exception{
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate", "Joshua", 26, true);
        Assert.notNull(soldierId, "createNewSoldier returned null ID");
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == 1, "In createNewTestGroupShouldReturnId: unexpected soldierPopulation value after solider addition");
    }

    @Test 
    void getSoldierByIdShouldReturnSoldier() throws Exception{
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate", "Joshua", 26, true);
        Soldier soldier = acftManagerService.getSoldierById(soldierId);
        Assert.isTrue(soldier.getId() == soldierId, "getSoldierById returned the incorrect Soldier");
    }

    //Tests like this passed when run alone, but failed when all methods in the class are executed
    //Added initial query to establish reference
    //This is perhaps a lazy solution to mitigate a lack of familiarity with how Spring Boot tests execute
    @Test
    void getAllTestGroupPseudoIdsShouldReturnAllExistingTestGroupPseudoIds() throws Exception{
        int reference = acftManagerService.getAllTestGroupPseudoIds().size();
        int n = 5;
        for (int i = 0; i < n; i++) acftManagerService.createNewTestGroup();
        List<Long> allExistingTestGroupIds = acftManagerService.getAllTestGroupPseudoIds();
        Assert.isTrue(allExistingTestGroupIds.size() == reference + n, "getAllTestGroups returned array of unexpected size");
    }

    @Test
    void getSoldiersByTestGroupIdShouldReturnListOfSoldiersWithPassedId() throws Exception{
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
    void updateSoldierScoreShouldReturnCorrectScaledScore() throws Exception{
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
    void deleteTestGroupsOnScheduleDeletesExpiredTestGroups() throws Exception{
        int reference = acftManagerService.getAllTestGroupPseudoIds().size();
        Long testGroupId1 = acftManagerService.createNewTestGroup();
        Long testGroupId2 = acftManagerService.createNewTestGroup();
        TestGroup testGroup1 = acftManagerService.getTestGroup(testGroupId1, "");
        TestGroup testGroup2 = acftManagerService.getTestGroup(testGroupId2, "");
        testGroup1.setExpirationDate(Date.from(Instant.now().minus(5, ChronoUnit.DAYS)));
        testGroup2.setExpirationDate(Date.from(Instant.now().plus(5, ChronoUnit.DAYS)));
        acftManagerService.deleteTestGroupsOnSchedule();
        Assert.isTrue(acftManagerService.getAllTestGroupPseudoIds().size() == reference + 1, "deleteTestGroupsOnSchedule did not produce expected results. Expected repo size " + 1 + ", size was actually " + acftManagerService.getAllTestGroupPseudoIds().size());
    }
    


    @Test
    void getXlsxFileForTestGroupDataGetsExpectedFile() throws Exception{
        int size = 5;
        Long testGroupId = acftManagerService.populateDatabase(size);
        File file = acftManagerService.getXlsxFileForTestGroupData(testGroupId, "");
        Assert.isTrue(file.getName().equals("testGroup_" + testGroupId + ".xlsx"), "In getXlsxFileForTestGroupDataGetsExpectedFile: File not found");
        file.delete();
    }

    @Test
    void flushDatabaseDeletesAllEntities() throws Exception{
        Long soldierReference = acftManagerService.getSoldierRepositorySize();
        Long testGroupReference = acftManagerService.getTestGroupRepositorySize();
        int size = 5;
        acftManagerService.populateDatabase(size);
        Assert.isTrue(acftManagerService.getSoldierRepositorySize() > soldierReference && acftManagerService.getTestGroupRepositorySize() > testGroupReference, "In flushDatabseDeletesAllEntities: database population failed");
        acftManagerService.flushDatabase();
        Assert.isTrue(acftManagerService.getTestGroupRepositorySize() == 0 && acftManagerService.getSoldierRepositorySize() == 0, "In flushDatabseDeletesAllEntities: flushDatabase() failed");
    }

    @Test
    @Transactional
    void deleteSoldiersByIdPersistsDeletion() throws Exception{
        String passcode = "";
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate", "Joshua", 26, true);
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == 1, "In deleteSoldiersByIdPersistsDeletion: unexpected TestGroup population size after soldier instantiation");
        acftManagerService.deleteSoldierById(testGroupId, passcode, soldierId);
        System.out.println("pop size: " + acftManagerService.getTestGroup(testGroupId, passcode).getSoldierPopulation().size());
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == 0, "In deleteSoldiersByIdPersistsDeletion: unexpected TestGroup population size " + acftManagerService.getTestGroup(testGroupId, passcode).getSoldierPopulation().size() + " after soldier deletion");
    }

    @Test
    void getTestGroupDataReturnsExpectedData() throws Exception{
        int size = 5;
        Long testGroupId = acftManagerService.populateDatabase(5);
        List<List<Long>> testGroupData = acftManagerService.getTestGroupScoreData(testGroupId, false);
        Assert.isTrue(testGroupData.size() == size && testGroupData.get(0).size() == 8, "In getTestGroupDataReturnsExpectedData: data array had unexpected dimensions");
        System.out.println("=================== TestGroup Data (Service Test) ===================");
        testGroupData.forEach((row) -> {
            row.forEach((element) -> {
                System.out.print(element + " ");
            });
            System.out.println();
        });
    }

    @Test
    void getBulkUploadTemplateReturnsFile() throws Exception{
        File file = acftManagerService.getBulkUploadTemplate();
        Assert.notNull(file, "In getBulkUploadTemplateReturnsFile: File not found");
    }

    @Test
    void instantiateBulkUploadDataInstantiatesSoldiers() throws Exception{
        File file = new File(testPath);
        int n = 10;
        bulkSoldierUploadTest.generateBulkUploadTestFile(n);
        String passcode = "passcode";
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long testGroupIdProtected = acftManagerService.createNewTestGroup(passcode);
        acftManagerService.instantiateBulkUploadData(file, testGroupId);
        file = new File(testPath);
        bulkSoldierUploadTest.generateBulkUploadTestFile(n);
        acftManagerService.instantiateBulkUploadData(file, testGroupIdProtected, passcode);
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == n, "In instantiateBulkUploadDataInstantiatesSoldiers: unexpected test group size after soldier instantiation");
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupIdProtected, passcode).size() == n, "In instantiateBulkUploadDataInstantiatesSoldiers: unexpected test group size after soldier instantiation; size was");
        file.delete();
    }

    @Test 
    void bulkSoldierUploadRejectsBadFiles() throws Exception{
        File file = new File(testPath);
        int n = 10;
        bulkSoldierUploadTest.generateBulkUploadTestFile(n);
        Long testGroupId = acftManagerService.createNewTestGroup();
        InputStream inputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);
        inputStream.close();
        OutputStream outputStream = new FileOutputStream(file);
        Sheet sheet = workbook.getSheetAt(0);
        Sheet originalSheet = sheet;
        Row row = sheet.getRow(2);
        row.getCell(0).setCellValue("");
        workbook.write(outputStream);
        Assert.isTrue(bulkUploadThrewException(file, testGroupId), "In bulkSoldierUploadRejectsBadFiles for Service: expected exception not thrown");
        sheet = originalSheet;
        row.getCell(2).setCellValue("data");
        workbook.write(outputStream);
        Assert.isTrue(bulkUploadThrewException(file, testGroupId), "In bulkSoldierUploadRejectsBadFiles for Service: expected exception not thrown");
        row.getCell(3).setCellValue("Q");
        workbook.write(outputStream);
        Assert.isTrue(bulkUploadThrewException(file, testGroupId), "In bulkSoldierUploadRejectsBadFiles for Service: expected exception not thrown");
        workbook.close();
        file.delete();
    }

    boolean bulkUploadThrewException(File file, Long testGroupId){
        boolean exceptionThrown = false;
        try {
            acftManagerService.instantiateBulkUploadData(file, testGroupId);
        } catch (Exception e){
            exceptionThrown = true;
        }
        return exceptionThrown;
    }
    
    @Test
    @Transactional
    void psuedoIdMechanismRecyclesIds() throws Exception{
        Long testGroupId = acftManagerService.createNewTestGroup();
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, "");
        testGroup.setExpirationDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
        acftManagerService.deleteTestGroupsOnSchedule();
        System.out.println("After scheduled deletion in pseudoIdTest, test group queue size is " + acftManagerService.getTestGroupPseudoIdQueue().size());
        Long newTestGroupId = acftManagerService.createNewTestGroup();
        TestGroup newTestGroup = acftManagerService.getTestGroup(newTestGroupId, "");
        System.out.println("Pseudo: " + newTestGroup.getPseudoId() + ", ID: " + testGroup.getId());
        Assert.isTrue(newTestGroup.getPseudoId() == testGroup.getId(), "In psuedoIdMechanismRecyclesIds: pseudo ID of new group was " + newTestGroup.getPseudoId() + " while ID of deleted testGroup was " + testGroup.getId());
        Long soldierId = acftManagerService.createNewSoldier(newTestGroupId, "Tate", "Joshua", 26, true);
        Soldier soldier = acftManagerService.getSoldierById(soldierId);
        acftManagerService.deleteSoldierById(newTestGroupId, "", soldierId);
        Long newSoldierId = acftManagerService.createNewSoldier(newTestGroupId, "Tate", "Joshua", 26, true);
        Soldier newSoldier = acftManagerService.getSoldierById(newSoldierId);
        Assert.isTrue(newSoldier.getPseudoId() == soldier.getId(), "In psuedoIdMechanismRecyclesIds: pseudo ID of new soldier did not match ID of deleted soldier");

    }


    
}
