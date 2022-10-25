package com.acft.acft;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AcftManagerServiceController {

    @Autowired
    AcftManagerService acftManagerService;
    
    @PostMapping("/testGroup/new")
    Long postNewTestGroup(){
        return acftManagerService.createNewTestGroup();
    }

    @GetMapping("/testGroup/get/{testGroupId}")
    TestGroup getTestGroup(@PathVariable Long testGroupId){
        return acftManagerService.getTestGroup(testGroupId);
    }

    @PostMapping("/testGroup/post/{testGroup}/{lastName}/{firstName}/{age}/{isMale}")
    Long createNewSoldier(@PathVariable TestGroup testGroup, @PathVariable String lastName, @PathVariable String firstName, @PathVariable int age, @PathVariable boolean isMale){
        return acftManagerService.createNewSoldier(testGroup, lastName, firstName, age, isMale);
    }

    @GetMapping("/soldier/get/{soldierId}")
    Soldier getSoldierById(@PathVariable Long soldierId){
        return acftManagerService.getSoldierById(soldierId);
    }

    @GetMapping("/testGroup/getSoldiers/{testGroupId}")
    List<Soldier> getSoldiersByTestGroupId(@PathVariable Long testGroupId){
        return acftManagerService.getSoldiersByTestGroupId(testGroupId);
    }

    @GetMapping("/testGroup/get/byLastNameAndGroup/{lastName}/{testGroupId}")
    List<Soldier> getSoldiersByLastNameAndTestGroupId(@PathVariable String lastName, @PathVariable Long testGroupId){
        return acftManagerService.getSoldiersByLastNameAndTestGroupId(lastName, testGroupId);
    }

    @GetMapping("/testGroup/get/all")
    List<Long> getAllTestGroupIds(){
        return acftManagerService.getAllTestGroups();
    }

    @PostMapping("/soldier/updateScore/{soldierId}/{eventId}/{rawScore}")
    int updateSoldierScore(@PathVariable Long soldierId, @PathVariable int eventId, @PathVariable int rawScore){
        return acftManagerService.updateSoldierScore(soldierId, eventId, rawScore);
    }
}
