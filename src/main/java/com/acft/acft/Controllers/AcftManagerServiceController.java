package com.acft.acft.Controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acft.acft.Entities.Soldier;
import com.acft.acft.Entities.TestGroup;
import com.acft.acft.Services.AcftManagerService;

@RestController
public class AcftManagerServiceController {

    @Autowired
    AcftManagerService acftManagerService;

    @PostMapping("/testGroup/new/{passcode}")
    Long postNewTestGroup(@PathVariable String passcode){
        //No psuedo IDs passed; all queries done by true ID
        Long testGroupId = acftManagerService.createNewTestGroup(passcode);
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, passcode);
        return testGroup.getPseudoId();
    }
    
    @PostMapping("/testGroup/new")
    Long postNewTestGroup(){
        return postNewTestGroup("");
    }

    @GetMapping("/testGroup/get/{pseudoTestGroupId}/{passcode}")
    TestGroup getTestGroup(@PathVariable Long pseudoTestGroupId, @PathVariable String passcode){
        //pseudo test group ID passed; queries performed by pseudo ID
        return acftManagerService.getTestGroupByPseudoId(pseudoTestGroupId, passcode);
    }

    @PostMapping("/testGroup/post/{pseudoTestGroupId}/{passcode}/{lastName}/{firstName}/{age}/{isMale}")
    Long createNewSoldier(@PathVariable Long pseudoTestGroupId, @PathVariable String passcode, @PathVariable String lastName, @PathVariable String firstName, @PathVariable int age, @PathVariable boolean isMale){
        Long soldierId = acftManagerService.createNewSoldier(pseudoTestGroupId, passcode, lastName, firstName, age, isMale);
        Soldier soldier = acftManagerService.getSoldierById(soldierId, passcode);
        return soldier.getPseudoId();
    }

    @PostMapping("/testGroup/post/{psuedoTestGroupId}/{lastName}/{firstName}/{age}/{isMale}")
    Long createNewSoldier(@PathVariable Long pseudoTestGroupId, @PathVariable String lastName, @PathVariable String firstName, @PathVariable int age, @PathVariable boolean isMale){
        return createNewSoldier(pseudoTestGroupId, "", lastName, firstName, age, isMale);
    }

    @GetMapping("/soldier/get/{soldierId}/{passcode}")
    Soldier getSoldierById(@PathVariable Long soldierId, @PathVariable String passcode){
        Soldier soldier = acftManagerService.getSoldierById(soldierId, passcode);
        //Throws exception to be caught by controller advice
        acftManagerService.getTestGroup(soldier.getTestGroup().getId(), passcode);
        return acftManagerService.getSoldierById(soldierId);
    }

    @GetMapping("/soldier/get/{soldierId}")
    Soldier getSoldierById(@PathVariable Long soldierId){
        return acftManagerService.getSoldierById(soldierId);
    }

    @GetMapping("/testGroup/getSoldiers/{pseudoTestGroupId}/{passcode}")
    List<Soldier> getSoldiersByTestGroupId(@PathVariable Long pseudoTestGroupId, @PathVariable String passcode){
        TestGroup testGroup = acftManagerService.getTestGroupByPseudoId(pseudoTestGroupId, passcode);
        return acftManagerService.getSoldiersByTestGroupId(testGroup.getId(), passcode);
    }

    @GetMapping("/testGroup/get/all")
    List<Long> getAllTestGroupIds(){
        return acftManagerService.getAllTestGroupPseudoIds();
    }

    @PostMapping("/soldier/updateScore/{soldierId}/{eventId}/{rawScore}/{passcode}")
    int updateSoldierScore(@PathVariable Long soldierId, @PathVariable int eventId, @PathVariable int rawScore, @PathVariable String passcode){
        return acftManagerService.updateSoldierScore(soldierId, eventId, rawScore, passcode);
    }

    @GetMapping("/testGroup/getXlsxFile/{testGroupId}/{passcode}")
    public void exportXlsxFileForTestGroup(HttpServletRequest request, HttpServletResponse response, @PathVariable Long testGroupId, @PathVariable String passcode){
        File file = acftManagerService.getXlsxFileForTestGroupData(testGroupId, passcode);
        exportXlsxFileUtility(request, response, file, true);
    }

    @GetMapping("/testGroup/getXlsxFile/{testGroupId}")
    public void exportXlsxFileForTestGroup(HttpServletRequest request, HttpServletResponse response, @PathVariable Long testGroupId){
        exportXlsxFileForTestGroup(request, response, testGroupId, "");
    }

    @GetMapping("/getBulkUploadTemplate")
    public void getBulkUploadTemplate(HttpServletRequest request, HttpServletResponse response){
        File file = acftManagerService.getBulkUploadTemplate();
        exportXlsxFileUtility(request, response, file, false);
    }

    //Might not need the response parater
    @PostMapping("/bulkUpload/{testGroupId}/{passcode}")
    public boolean bulkUpload(HttpServletRequest request, HttpServletResponse response, @PathVariable Long testGroupId, @PathVariable String passcode){
        String path = "src/main/resources/data/bulkUpload.xlsx";
        File file = new File(path);
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(path);
            FileCopyUtils.copy(request.getInputStream(), outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        file = new File(path);
        return acftManagerService.instantiateBulkUploadData(file, testGroupId, passcode);
        
    }

    @PostMapping("/bulkUpload/{testGroupId}")
    public boolean bulkUpload(HttpServletRequest request, HttpServletResponse response, @PathVariable Long testGroupId){
        return bulkUpload(request, response, testGroupId, "");
    }

    //Might not need the request parameter
    public void exportXlsxFileUtility(HttpServletRequest request, HttpServletResponse response, File file, boolean delete){
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
        response.setContentLength((int) file.length());
        InputStream inputStream;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e){
            System.out.println(e.getMessage());
        }   
        if (delete) file.delete();
    }

    @DeleteMapping("/deleteAll")
    public boolean flushDatabase(){
        return acftManagerService.flushDatabase();
    }

    @DeleteMapping("/soldier/delete/{pseudoTestGroupId}/{soldierId}/{passcode}")
    public boolean deleteSoldierById(@PathVariable Long pseudoTestGroupId, @PathVariable Long soldierId, @PathVariable String passcode){
        TestGroup testGroup = acftManagerService.getTestGroupByPseudoId(pseudoTestGroupId, passcode);
        return acftManagerService.deleteSoldierById(testGroup.getId(), passcode, soldierId);
    }

    @DeleteMapping("/soldier/delete/{pseudoTestGroupId}/{soldierId}")
    public boolean deleteSoldierById(@PathVariable Long pseudoTestGroupId, @PathVariable Long soldierId){
        return deleteSoldierById(pseudoTestGroupId, soldierId, "");
    }

    @GetMapping("/testGroup/{pseudoTestGroupId}/get/scoreData/{raw}/{passcode}")
    public List<List<Long>> getTestGroupScoreData(@PathVariable Long pseudoTestGroupId, @PathVariable boolean raw, @PathVariable String passcode){
        TestGroup testGroup = acftManagerService.getTestGroupByPseudoId(pseudoTestGroupId, passcode);
        return acftManagerService.getTestGroupScoreData(testGroup.getId(), passcode, raw);
    }

    @GetMapping("/testGroup/{pseudoTestGroupId}/get/scoreData/{raw}")
    public List<List<Long>> getTestGroupScoreData(@PathVariable Long pseudoTestGroupId, @PathVariable boolean raw){
        return getTestGroupScoreData(pseudoTestGroupId, raw, "");
    }

    @PostMapping("/populateDatabase/{size}")
    public Long populateDatabase(@PathVariable int size){
        Long testGroupId = acftManagerService.populateDatabase(size);
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, "");
        System.out.println("Created test group with pseudo ID " + testGroup.getPseudoId());
        return testGroup.getPseudoId();
    }




}
