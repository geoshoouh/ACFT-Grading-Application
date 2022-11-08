package com.acft.acft.Controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    
    @PostMapping("/testGroup/new")
    Long postNewTestGroup(){
        return acftManagerService.createNewTestGroup();
    }

    @PostMapping("/testGroup/new/{passcode}")
    Long postNewTestGroup(@PathVariable String passcode){
        return acftManagerService.createNewTestGroup(passcode);
    }

    @GetMapping("/testGroup/get/{testGroupId}/{passcode}")
    TestGroup getTestGroup(@PathVariable Long testGroupId, @PathVariable String passcode){
        return acftManagerService.getTestGroup(testGroupId, passcode);
    }


    @PostMapping("/testGroup/post/{testGroupId}/{passcode}/{lastName}/{firstName}/{age}/{isMale}")
    Long createNewSoldier(@PathVariable Long testGroupId, @PathVariable String passcode, @PathVariable String lastName, @PathVariable String firstName, @PathVariable int age, @PathVariable boolean isMale){
        return acftManagerService.createNewSoldier(testGroupId, passcode, lastName, firstName, age, isMale);
    }

    @PostMapping("/testGroup/post/{testGroupId}/{lastName}/{firstName}/{age}/{isMale}")
    Long createNewSoldier(@PathVariable Long testGroupId, @PathVariable String lastName, @PathVariable String firstName, @PathVariable int age, @PathVariable boolean isMale){
        return acftManagerService.createNewSoldier(testGroupId, lastName, firstName, age, isMale);
    }

    @GetMapping("/soldier/get/{soldierId}/{passcode}")
    Soldier getSoldierById(@PathVariable Long soldierId, @PathVariable String passcode){
        return acftManagerService.getSoldierById(soldierId, passcode);
    }

    @GetMapping("/soldier/get/{soldierId}")
    Soldier getSoldierById(@PathVariable Long soldierId){
        return acftManagerService.getSoldierById(soldierId);
    }

    @GetMapping("/testGroup/getSoldiers/{testGroupId}/{passcode}")
    List<Soldier> getSoldiersByTestGroupId(@PathVariable Long testGroupId, @PathVariable String passcode){
        return acftManagerService.getSoldiersByTestGroupId(testGroupId, passcode);
    }

    @GetMapping("/testGroup/get/all")
    List<Long> getAllTestGroupIds(){
        return acftManagerService.getAllTestGroups();
    }

    @PostMapping("/soldier/updateScore/{soldierId}/{eventId}/{rawScore}/{passcode}")
    int updateSoldierScore(@PathVariable Long soldierId, @PathVariable int eventId, @PathVariable int rawScore, @PathVariable String passcode){
        return acftManagerService.updateSoldierScore(soldierId, eventId, rawScore, passcode);
    }

    @GetMapping("/testGroup/getXlsxFile/{testGroupId}/{passcode}")
    public void exportXlsxFileForTestGroup(HttpServletRequest request, HttpServletResponse response, @PathVariable Long testGroupId, @PathVariable String passcode){
        File file = acftManagerService.getXlsxFileForTestGroupData(testGroupId, passcode);
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
        file.delete();
    }

    @GetMapping("/testGroup/getXlsxFile/{testGroupId}")
    public void exportXlsxFileForTestGroup(HttpServletRequest request, HttpServletResponse response, @PathVariable Long testGroupId){
        File file = acftManagerService.getXlsxFileForTestGroupData(testGroupId, "");
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
        file.delete();
    }

    @DeleteMapping("/deleteAll")
    public boolean flushDatabase(){
        return acftManagerService.flushDatabase();
    }
}
