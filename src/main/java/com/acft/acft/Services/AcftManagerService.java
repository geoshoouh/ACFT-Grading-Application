package com.acft.acft.Services;

import java.util.Date;
import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.acft.acft.Entities.Soldier;
import com.acft.acft.Entities.TestGroup;
import com.acft.acft.Exceptions.InvalidPasscodeException;
import com.acft.acft.Exceptions.SoldierNotFoundException;
import com.acft.acft.Exceptions.TestGroupNotFoundException;
import com.acft.acft.Repositories.SoldierRepository;
import com.acft.acft.Repositories.TestGroupRepository;


@Service
public class AcftManagerService {
    
    @Autowired
    private TestGroupRepository testGroupRepository;

    @Autowired
    private SoldierRepository soldierRepository;

    @Autowired
    private AcftDataConversion acftDataConversion;

    @Autowired
    private AcftDataExporter acftDataExporter;


    public Long createNewTestGroup(){
        TestGroup testGroup = new TestGroup();
        testGroupRepository.save(testGroup);
        return testGroup.getId();
    }

    public Long createNewTestGroup(String passcode){
        TestGroup testGroup = new TestGroup(passcode);
        testGroupRepository.save(testGroup);
        return testGroup.getId();
    }

    public TestGroup getTestGroup(Long testGroupId, String passcode) throws TestGroupNotFoundException, InvalidPasscodeException{
        TestGroup testGroup = testGroupRepository.findById(testGroupId)
            .orElseThrow(() -> new TestGroupNotFoundException(testGroupId));
        if (!passcode.equals(testGroup.getPasscode())) throw new InvalidPasscodeException(testGroupId);
        return testGroup;
    }

    public TestGroup getTestGroup(Long testGroupId) throws TestGroupNotFoundException, InvalidPasscodeException{
        TestGroup testGroup = testGroupRepository.findById(testGroupId)
            .orElseThrow(() -> new TestGroupNotFoundException(testGroupId));
        if (testGroup.getPasscode().length() > 0) throw new InvalidPasscodeException(testGroupId);
        return testGroup;
    }

    public Long createNewSoldier(TestGroup testGroup, String lastName, String firstName, int age, boolean isMale){
        Soldier soldier = new Soldier(testGroup, lastName, firstName, age, isMale);
        soldierRepository.save(soldier);
        return soldier.getId();
    }

    public Soldier getSoldierById(Long soldierId, String passcode) throws SoldierNotFoundException, InvalidPasscodeException{
        Soldier soldier = soldierRepository.findById(soldierId)
            .orElseThrow(() -> new SoldierNotFoundException(soldierId));
        getTestGroup(soldier.getTestGroupId(), passcode);
        return soldier;
    }

    public Soldier getSoldierById(Long soldierId) throws SoldierNotFoundException, InvalidPasscodeException{
        Soldier soldier =  soldierRepository.findById(soldierId)
            .orElseThrow(() -> new SoldierNotFoundException(soldierId));
        getTestGroup(soldier.getTestGroupId());
        return soldier;
    }

    public List<Soldier> getSoldiersByLastNameAndTestGroupId(String lastName, Long testGroupId){
        return soldierRepository.findByLastNameAndTestGroupId(lastName, testGroupId);
    }

    public List<Soldier> getSoldiersByTestGroupId(Long testGroupId, String passcode) throws TestGroupNotFoundException, InvalidPasscodeException{
        getTestGroup(testGroupId, passcode);
        return soldierRepository.findByTestGroupId(testGroupId);
    }

    public List<Soldier> getSoldiersByTestGroupId(Long testGroupId) throws TestGroupNotFoundException, InvalidPasscodeException{
        getTestGroup(testGroupId);
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

    public int updateSoldierScore(Long soldierId, int eventId, int rawScore, String passcode) throws SoldierNotFoundException, InvalidPasscodeException{
        Soldier soldier = getSoldierById(soldierId);
        //Throws Invalid passode exception
        getTestGroup(soldier.getTestGroupId(), passcode);
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
        soldierRepository.save(soldier);
        return scaledScore;
    }
    
    public int updateSoldierScore(Long soldierId, int eventId, int rawScore) throws SoldierNotFoundException, InvalidPasscodeException{
        Soldier soldier = getSoldierById(soldierId);
        //Throws Invalid passode exception
        getTestGroup(soldier.getTestGroupId());
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
        soldierRepository.save(soldier);
        return scaledScore;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void deleteTestGroupsOnSchedule(){
        Date cutoff = Date.from(Instant.now().minus(2, ChronoUnit.DAYS));
        System.out.println("Cutoff date: " + cutoff);
        List<TestGroup> expiredTestGroups = testGroupRepository.findByExpirationDateBefore(cutoff);
        System.out.println("size of tg pull is: " + expiredTestGroups.size());
        expiredTestGroups.forEach((group) -> System.out.println(group.toString()));
        expiredTestGroups.forEach((testGroup) -> {
            testGroup.getSoldierPopulation().forEach((soldier) -> {
                soldierRepository.delete(soldier);
            });
            testGroupRepository.delete(testGroup);
        });
    }

    public Long populateDatabase(){
        Long testGroupId = createNewTestGroup();
        System.out.println("TGID: " + testGroupId);
        TestGroup testGroup = getTestGroup(testGroupId);
        int n = 5;
        Long[] soldierIds = new Long[5];
        String[] lastNames = {"Smith", "Jones", "Samuels", "Smith", "Conway"};
        String[] firstNames = {"Jeff", "Timothy", "Darnell", "Fredrick", "Katherine"};
        int[] ages = {26, 18, 19, 30, 23};
        boolean[] genders = {true, true, true, true, false};
        for (int i = 0; i < n; i++){
            soldierIds[i] = createNewSoldier(testGroup, lastNames[i], firstNames[i], ages[i], genders[i]);
            for (int j = 0; j < 6; j++){
                updateSoldierScore(soldierIds[i], j, AcftDataConversion.generateRandomRawScore(j));
            }
        }
        return testGroupId;
    }

    public File getXlsxFileForTestGroupData(Long testGroupId, String passcode) throws TestGroupNotFoundException, InvalidPasscodeException{
        //Line below will throw exception if the test group doesn't exist or the user does not have the correct passcode
        getTestGroup(testGroupId, passcode);
        List<Soldier> soldiers = getSoldiersByTestGroupId(testGroupId, passcode);
        XSSFWorkbook workbook = acftDataExporter.createXlsxWorkbook(soldiers);
        String path = acftDataExporter.createXlsxFile(workbook, testGroupId);
        File file = new File(path);
        return file;
    }

    public File getXlsxFileForTestGroupData(Long testGroupId) throws TestGroupNotFoundException, InvalidPasscodeException{
        //Line below will throw exception if the test group doesn't exist or the user does not have the correct passcode
        getTestGroup(testGroupId);
        List<Soldier> soldiers = getSoldiersByTestGroupId(testGroupId);
        XSSFWorkbook workbook = acftDataExporter.createXlsxWorkbook(soldiers);
        String path = acftDataExporter.createXlsxFile(workbook, testGroupId);
        File file = new File(path);
        return file;
    }

}
