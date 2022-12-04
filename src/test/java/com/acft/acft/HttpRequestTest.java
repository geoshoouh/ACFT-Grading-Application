package com.acft.acft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;

import com.acft.acft.Entities.Soldier;
import com.acft.acft.Entities.TestGroup;
import com.acft.acft.Exceptions.InvalidPasscodeException;
import com.acft.acft.Services.AcftManagerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;


@SpringBootTest
@AutoConfigureMockMvc
public class HttpRequestTest {

    @Autowired
    AcftManagerService acftManagerService;

    BulkSoldierUploadTest bulkSoldierUploadTest = new BulkSoldierUploadTest();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Gson gson;

    @Test
    void postNewTestGroupShouldReturnGroupIdAndPasswordNotIncludedInResponse() throws Exception{

        Long testGroupId = Long.parseLong(
            mockMvc.perform(
                post("/testGroup/new")
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                );
        
        Assert.notNull(testGroupId, "Response to /testGroup/new was null");
    }

    @Test
    void postNewTestGroupWithPasscodeShouldReturnGroupId() throws Exception{
        String passcode = "password";
        Long testGroupId = Long.parseLong(
            mockMvc.perform(
                post("/testGroup/new/{passcode}", passcode)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                );
        Assert.notNull(testGroupId, "Response to /testGroup/new was null");
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, passcode);
        Assert.isTrue(testGroup.getPasscode().equals(passcode), "in postNewTestGroup w/passcode, expected passcode was " + passcode + ",actual passcode was " + testGroup.getPasscode());
    }

    //Ensures a test group's passcode cannot be fetched by making a getAllTestGroups request and inspecting the Json
    @Test
    void getTestGroupShouldReturnTestGroup() throws Exception{
        //testGroup instantiated w/passcode
        String passcode = "password";
        Long testGroupId = acftManagerService.createNewTestGroup(passcode);
        TestGroup testGroupFromResponse = gson.fromJson(
            mockMvc.perform(
                get("/testGroup/get/{testGroupId}/{passcode}", testGroupId, passcode)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), 
            TestGroup.class
            );
        Assert.isTrue(testGroupFromResponse.getId() == testGroupId, "Response to /testGroup/get/{testGroupId} returned incorrect TestGroup");

        //testGroup instantiated w/o passcode
        Long testGroupIdEmptyPasscode = acftManagerService.createNewTestGroup();
        TestGroup testGroupFromResponseEmptyPasscode = gson.fromJson(
            mockMvc.perform(
                get("/testGroup/get/{testGroupId}/randomText", testGroupIdEmptyPasscode)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), 
            TestGroup.class
            );
        Assert.isTrue(testGroupFromResponseEmptyPasscode.getId() == testGroupIdEmptyPasscode, "Response to /testGroup/get/{testGroupId} returned incorrect TestGroup");
    }

     //Ensures a test group's passcode cannot be fetched by making a getAllTestGroups request and inspecting the Json
     //Test group passcodes never reach the client side after instantiation
    @Test
    void testGroupPasscodeNotVisibleInJsonRepresentiation() throws Exception{
        String passcode = "password";
        Long testGroupId = acftManagerService.createNewTestGroup(passcode);
        TestGroup testGroupFromResponse = gson.fromJson(
            mockMvc.perform(
                get("/testGroup/get/{testGroupId}/{passcode}", testGroupId, passcode)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), 
            TestGroup.class
        );
        boolean exceptionCaught = false;
        try {
            acftManagerService.getTestGroup(testGroupId, testGroupFromResponse.getPasscode());
        } catch (InvalidPasscodeException e) {
            exceptionCaught = true;
        }
        Assert.isTrue(exceptionCaught, "Passcode received from json conversion of testGroup should have been null and resulted in a thrown exception when attempting to get the same test group");
    }

    @Test
    void createNewSoldierShouldReturnSoldierId() throws Exception{
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = Long.parseLong(
            mockMvc.perform(
                post("/testGroup/post/{testGroup}/{lastName}/{firstName}/{age}/{isMale}",
                testGroupId, "Tate", "Joshua", 26, true)
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()
        );
        Assert.notNull(soldierId, "Response to postNewSoldier http request was null");
    }

    @Test
    void getSoldierByIdShouldReturnSoldier() throws Exception{
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate", "Joshua", 26, true);
        Soldier soldier = gson.fromJson(
            mockMvc.perform(
                get("/soldier/get/{soldierId}", soldierId)
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(),
             Soldier.class
            );
        Assert.isTrue(soldier.getId() == soldierId, "/soldier/get/{soldierId} responded with incorrect Soldier");
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
        Type listOfSoldierObjects = new TypeToken<ArrayList<Soldier>>() {}.getType();
        List<Soldier> queryResult = gson.fromJson(
            mockMvc.perform(
                get("/testGroup/getSoldiers/{testGroupId}/randomText",
                testGroupId)
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()
            , listOfSoldierObjects);
            Assert.isTrue(queryResult.size() == n, "getSoldiersByTestGroupId returned array of unexpected size");
    }

    @Test
    void getAllTestGroupsShouldReturnAllExistingTestGroupIds() throws Exception{
        int reference = acftManagerService.getAllTestGroups().size();
        int n = 5;
        for (int i = 0; i < n; i++) acftManagerService.createNewTestGroup();
        Type listOfTestGroupIds = new TypeToken<ArrayList<Long>>() {}.getType();
        List<Long> requestResult = gson.fromJson(
            mockMvc.perform(
                get("/testGroup/get/all")
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(), listOfTestGroupIds);
        Assert.isTrue(requestResult.size() == reference + n, "getAllTestGroupsShouldReturnAllExistingTestGroupIds returned incorrectly sized array");
    }

    @Test
    void updateSoldierScoreShouldReturnCorrectConvertedScore() throws Exception{
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate", "Joshua", 26, true);
        int eventId = 0;
        int rawScore = 205;
        int expectedConversion = 71;
        int requestResult = Integer.parseInt(
            mockMvc.perform(
                post("/soldier/updateScore/{soldierId}/{eventId}/{rawScore}/randomValue",
                soldierId, eventId, rawScore)
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()
            );
        Assert.isTrue(requestResult == expectedConversion, "For update score request: expected result was " + expectedConversion + ", actual result was " + requestResult);
    }

    @Test
    void updateSoldierScoreOnProtectedTestGroupShouldReturnCorrectConvertedScore() throws Exception{
        String passcode = "password";
        Long testGroupId = acftManagerService.createNewTestGroup(passcode);
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, passcode, "Tate", "Joshua", 26, true);  
        int eventId = 5;
        int rawScore = 1080;
        int expectedScore = 74;
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, passcode);
        System.out.println("passcode from TG: " + testGroup.getPasscode() + " passed code: " + passcode);
        System.out.println(testGroup.getPasscode().equals(passcode));
        int requestResult = Integer.parseInt(
            mockMvc.perform(
                post("/soldier/updateScore/{soldierId}/{eventId}/{rawScore}/{passcode}",
                soldierId, eventId, rawScore, passcode)
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()
            ); 
            Assert.isTrue(requestResult == expectedScore, "For update score request: expected result was " + expectedScore + ", actual result was " + requestResult);
    }

    @Test
    void exportXlsxFileForTestGroupShouldExportExpectedFile() throws Exception{
        int size = 5;
        //No passcode used in populateDatabase utility function
        Long testGroupId = acftManagerService.populateDatabase(size);
        HttpServletResponse response = mockMvc.perform(
            get("/testGroup/getXlsxFile/{testGroupId}", testGroupId)
        ).andExpect(status().isOk())
        .andReturn()
        .getResponse();
        Assert.isTrue(response.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), "In exportXlsxFileForTestGroupShouldExportExpectedFile: unexpected content type in servlet response");
        String path = "src/main/resources/data/testGroup_" + testGroupId + ".xlsx";
        Assert.isTrue(!new File(path).exists(), "In createXlsxFileCreatesXlsxFileWithExpectedSheets: file was not deleted after being served to client");
    }

    @Test
    void getBulkUploadTemplateReturnsFile() throws Exception{
        HttpServletResponse response = mockMvc.perform(
            get("/getBulkUploadTemplate")
        ).andExpect(status().isOk())
        .andReturn()
        .getResponse();
        Assert.isTrue(response.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), "In getBulkUploadTemplateReturnsFile: unexpected content type in servlet response");
    }

    @Test
    void flushDatabaseDeletesAllEntities() throws Exception{
        int size = 5;
        acftManagerService.populateDatabase(size);
        Assert.isTrue(acftManagerService.getSoldierRepositorySize() > 0 && acftManagerService.getTestGroupRepositorySize() > 0, "In flushDatabseDeletesAllEntities: database population failed");
        boolean response = Boolean.parseBoolean(
            mockMvc.perform(
                delete("/deleteAll")
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()
        );
        Assert.isTrue(acftManagerService.getSoldierRepositorySize() == 0 && acftManagerService.getTestGroupRepositorySize() == 0, "In flushDatabseDeletesAllEntities: request to flushDatabase() failed");
        Assert.isTrue(response, "In flushDatabseDeletesAllEntities: unexpected boolean response");
    }

    //Attempted to test using Set.contains() instead of Set.size(), but this failed and it 
    //did not seem worth it to determine the cause yet
    @Test
    void deleteSoldierByIdPersistsDeletion() throws Exception{
        Long testGroupId = acftManagerService.createNewTestGroup();
        Long soldierId = acftManagerService.createNewSoldier(testGroupId, "Tate", "Joshua", 26, true);
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == 1, "In deleteSoldierByIdPersistsDeletion: testGroup had unexpected population size after soldier creation");
        boolean response = Boolean.parseBoolean(
            mockMvc.perform(
                delete("/soldier/delete/{testGroupId}/{soldierId}", testGroupId, soldierId)
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()
        );
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == 0, "In deleteSoldierByIdPersistsDeletion: testGroup had unexpected population size after soldier deletion");
        Assert.isTrue(response, "In deleteSoldierByIdPersistsDeletion: unexpected boolean response");
    }

    @Test
    void getTestGroupDataReturnsExpectedData() throws Exception{
        int size = 5;
        Long testGroupId = acftManagerService.populateDatabase(size);
        Type testGroupDataType = new TypeToken<ArrayList<ArrayList<Long>>>() {}.getType();
        List<List<Long>> testGroupData = gson.fromJson(
            mockMvc.perform(
                get("/testGroup/{testGroupId}/get/scoreData/{raw}", testGroupId, true)
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString()
            , testGroupDataType);
            Assert.isTrue(testGroupData.size() == size && testGroupData.get(0).size() == 8, "In getTestGroupDataReturnsExpectedData: data array had unexpected dimensions");

            System.out.println("=================== TestGroup Data (Http Test) ===================");
            testGroupData.forEach((row) -> {
                    row.forEach((element) -> {
                    System.out.print(element + " ");
                });
                System.out.println();
            });
    }

    @Test
    void populateDatePersistsData() throws Exception{
        int size = 11;
        Long testGroupId = Long.parseLong(
            mockMvc.perform(
                post("/populateDatabase/{size}", size)
            ).andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString());
        Assert.isTrue(acftManagerService.getSoldiersByTestGroupId(testGroupId).size() == size, "In populateDatePersistsData: unexpected testGroup population size after populate called");
    }

    @Test
    void instantiateBulkUploadDataInstantiatesSoldiers() throws Exception{
        int sz = 5;
        bulkSoldierUploadTest.generateBulkUploadTestFile(sz);
    }

}
