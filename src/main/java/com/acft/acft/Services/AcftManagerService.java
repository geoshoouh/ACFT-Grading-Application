package com.acft.acft.Services;

import java.util.Date;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acft.acft.Entities.PseudoId;
import com.acft.acft.Entities.Soldier;
import com.acft.acft.Entities.TestGroup;
import com.acft.acft.Exceptions.InvalidBulkUploadException;
import com.acft.acft.Exceptions.InvalidPasscodeException;
import com.acft.acft.Exceptions.SoldierNotFoundException;
import com.acft.acft.Exceptions.TestGroupNotFoundException;
import com.acft.acft.Repositories.PseudoIdRepository;
import com.acft.acft.Repositories.SoldierRepository;
import com.acft.acft.Repositories.TestGroupRepository;


@Service
public class AcftManagerService {
    
    @Autowired
    private TestGroupRepository testGroupRepository;

    @Autowired
    private PseudoIdRepository pseudoIdRepository;

    @Autowired
    private SoldierRepository soldierRepository;

    @Autowired
    private AcftDataConversion acftDataConversion;

    @Autowired
    private AcftDataExporter acftDataExporter;
    

    public Long createNewTestGroup(){
        TestGroup testGroup = new TestGroup();
        testGroupRepository.save(testGroup);
        if (pseudoIdRepository.count() > 0){
            PseudoId pseudoId = pseudoIdRepository.findFirstByOrderByPseudoId();
            testGroup.setPseudoId(pseudoId.getPseudoId());
            pseudoIdRepository.deleteById(pseudoId.getId());
        } else{
            testGroup.setPseudoId(testGroup.getId());
        }
        testGroupRepository.save(testGroup);
        return testGroup.getId();
    }

    public Long createNewTestGroup(String passcode){
        TestGroup testGroup = new TestGroup(passcode);
        testGroupRepository.save(testGroup);
        if (pseudoIdRepository.count() > 0){
            PseudoId pseudoId = pseudoIdRepository.findFirstByOrderByPseudoId();
            testGroup.setPseudoId(pseudoId.getPseudoId());
            pseudoIdRepository.deleteById(pseudoId.getId());
        } else{
            testGroup.setPseudoId(testGroup.getId());
        }
        testGroupRepository.save(testGroup);
        return testGroup.getId();
    }

    
    public TestGroup getTestGroup(Long testGroupId, String passcode) throws TestGroupNotFoundException, InvalidPasscodeException{
        TestGroup testGroup = testGroupRepository.findById(testGroupId)
            .orElseThrow(() -> new TestGroupNotFoundException(testGroupId));
        if (testGroup.getPasscode().length() > 0 && !passcode.equals(testGroup.getPasscode())) throw new InvalidPasscodeException(testGroupId);
        return testGroup;
    }

    public Long createNewSoldier(Long testGroupId, String passcode, String lastName, String firstName, int age, boolean isMale) throws TestGroupNotFoundException, InvalidPasscodeException{
        TestGroup testGroup = getTestGroup(testGroupId, passcode);
        Soldier soldier = new Soldier(testGroup, lastName, firstName, age, isMale);
        soldierRepository.save(soldier);
        if (pseudoIdRepository.count() > 0){
            PseudoId pseudoId = pseudoIdRepository.findFirstByOrderByPseudoId();
            soldier.setPseudoId(pseudoId.getPseudoId());
            pseudoIdRepository.deleteById(pseudoId.getId());
        } else {
            soldier.setPseudoId(soldier.getId());
        }
        soldierRepository.save(soldier);
        return soldier.getId();
    }

    public Long createNewSoldier(Long testGroupId, String lastName, String firstName, int age, boolean isMale) throws TestGroupNotFoundException, InvalidPasscodeException{
        return createNewSoldier(testGroupId, "", lastName, firstName, age, isMale);
    }

    public Soldier getSoldierById(Long soldierId, String passcode) throws SoldierNotFoundException, InvalidPasscodeException{
        Soldier soldier = soldierRepository.findById(soldierId)
            .orElseThrow(() -> new SoldierNotFoundException(soldierId));
        getTestGroup(soldier.getTestGroup().getId(), passcode);
        return soldier;
    }

    //This method is to keep older test cased running properly. Many such cases do not implement password protection
    public Soldier getSoldierById(Long soldierId) throws SoldierNotFoundException, InvalidPasscodeException{
        return getSoldierById(soldierId, "");
    }

    public List<Soldier> getSoldiersByTestGroupId(Long testGroupId, String passcode) throws TestGroupNotFoundException, InvalidPasscodeException{
        getTestGroup(testGroupId, passcode);
        return soldierRepository.findByTestGroupIdOrderByLastNameAsc(testGroupId);
    }

    //Exists as a testing convenience
    public List<Soldier> getSoldiersByTestGroupId(Long testGroupId) throws TestGroupNotFoundException, InvalidPasscodeException{
        getTestGroup(testGroupId, "");
        return soldierRepository.findByTestGroupIdOrderByLastNameAsc(testGroupId);
    }
    
    public List<Long> getAllTestGroupPseudoIds(){
        List<TestGroup> allTestGroups =  testGroupRepository.findAllByOrderByPseudoId();
        List<Long> allTestGroupIds = new ArrayList<>();
        for (TestGroup testGroup : allTestGroups){
            allTestGroupIds.add(testGroup.getPseudoId());
        }
        return allTestGroupIds;
    }


    public int updateSoldierScore(Long soldierId, int eventId, int rawScore, String passcode) throws SoldierNotFoundException, TestGroupNotFoundException, InvalidPasscodeException{
        //getSoldier will throw invalid passcode exception
        Soldier soldier = getSoldierById(soldierId, passcode);
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

    @Transactional
    @Scheduled(fixedDelay = 6, timeUnit = TimeUnit.HOURS)
    public void deleteTestGroupsOnSchedule(){
        Date cutoff = Date.from(Instant.now());
        System.out.println("deleteTestGroupsOnSchedule executed for cutoff: " + cutoff);
        System.out.println("Cutoff date: " + cutoff);
        List<TestGroup> expiredTestGroups = testGroupRepository.findByExpirationDateBefore(cutoff);
        System.out.println("size of tg pull is: " + expiredTestGroups.size());
        expiredTestGroups.forEach((group) -> System.out.println(group.toString()));
        expiredTestGroups.forEach((testGroup) -> {
            List<Soldier> soldiers = soldierRepository.findByTestGroupIdOrderByLastNameAsc(testGroup.getId());
            soldiers.forEach((soldier) -> {pseudoIdRepository.save(new PseudoId(soldier.getPseudoId()));});
            testGroupRepository.delete(testGroup);
            pseudoIdRepository.save(new PseudoId(testGroup.getPseudoId()));
        });
    }


    public Long populateDatabase(int size){
        if (size > 100) size = 100;
        else if (size < 0) size = 0;
        GenerateRandomData generateRandomData = new GenerateRandomData();
        String passcode = "";
        Long testGroupId = createNewTestGroup();
        List<List<String>> names = generateRandomData.getNames(size);
        for (int i = 0; i < size; i++){
            Long soldierId = createNewSoldier(testGroupId, names.get(i).get(0), names.get(i).get(1), GenerateRandomData.generateRandomAge(), GenerateRandomData.generateRandomGender());
            for (int j = 0; j < 6; j++){
                updateSoldierScore(soldierId, j, GenerateRandomData.generateRandomRawScore(j), passcode);
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
        //Used to catch FileNotFoundException because File does not throw it
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.close();
        } catch (FileNotFoundException fileNotFoundException){
            file = null;
        } catch (IOException ioException){
            file = null;
        }
        return file;
    }

    public File getBulkUploadTemplate() {
        String path = "src/main/resources/data/bulkUploadTemplate.xlsx";
        File file = new File(path);
        //Used to catch FileNotFoundException because File does not throw it
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.close();
        } catch (FileNotFoundException fileNotFoundException){
            file = null;
        } catch (IOException ioException){
            file = null;
        }
        return file;
    }
   
    public boolean instantiateBulkUploadData(File file, Long testGroupId, String passcode) throws InvalidBulkUploadException, InvalidPasscodeException, TestGroupNotFoundException{
        List<List<String>> data;
        try {
            data = BulkSoldierUpload.stripBulkSoldierData(file);
        } catch (IOException e){
            System.out.println("In instantiateBulkUploadData in AcftManagerService: " +  e.getMessage());
            return false;
        }
        //IO Operation is good; make sure stripped data is valid
        try {
            BulkSoldierUpload.validateBulkUploadData(data);
        } catch (InvalidBulkUploadException e){
            System.out.println(e.getMessage());
            file.delete();
            throw new InvalidBulkUploadException();
        }
        
        //Valid; instantiate
        try {
            data.forEach((row) -> {
                createNewSoldier(
                    testGroupId, 
                    passcode, 
                    row.get(0), 
                    row.get(1), 
                    Integer.parseInt(row.get(2)), 
                    (row.get(3).equals("M") || row.get(3).equals("m")) ?  true : false);
            });
        } catch (InvalidPasscodeException e){
            System.out.println(e.getMessage());
            file.delete();
            throw new InvalidPasscodeException(testGroupId);
        }
        
        file.delete();
        return true;
    }

    public boolean instantiateBulkUploadData(File file, Long testGroupId) throws InvalidBulkUploadException, InvalidPasscodeException, TestGroupNotFoundException {
        return instantiateBulkUploadData(file, testGroupId, "");
    }

    public boolean flushDatabase(){
        List<TestGroup> testGroups = testGroupRepository.findAll();
        List<Soldier> soldiers = soldierRepository.findAll();
        for (TestGroup testGroup : testGroups) pseudoIdRepository.save(new PseudoId(testGroup.getPseudoId()));
        for (Soldier soldier : soldiers) pseudoIdRepository.save(new PseudoId(soldier.getPseudoId()));
        testGroupRepository.deleteAll();
        if (soldierRepository.count() == 0 && testGroupRepository.count() == 0) return true;
        return false;
    }


    public Long getSoldierRepositorySize(){
        return soldierRepository.count();
    }

    public Long getTestGroupRepositorySize(){
        return testGroupRepository.count();
    }

    public boolean deleteSoldierById(Long testGroupId, String passcode, Long soldierId) throws InvalidPasscodeException {
        //Check for TestGroup access
        getTestGroup(testGroupId, passcode);
        Soldier soldier;
        try {
            soldier = soldierRepository.findById(soldierId).orElseThrow(() -> new SoldierNotFoundException(soldierId));
        } catch (SoldierNotFoundException e){
            System.out.println(e);
            return false;
        }
        soldierRepository.delete(soldier);   
        pseudoIdRepository.save(new PseudoId(soldier.getPseudoId()));
        return true;
    }

    //Gets n x 8 array of scores with first column as soldier pseudo ID corresponding to the scores in the row
    public List<List<Long>> getTestGroupScoreData(Long testGroupId, String passcode, boolean raw) throws InvalidPasscodeException {
        List<List<Long>> data = new ArrayList<>();
        getSoldiersByTestGroupId(testGroupId, passcode).forEach((soldier) -> {
            List<Long> scores = new ArrayList<>();
            scores.add(soldier.getPseudoId());
            for (int score : soldier.getScoresAsArray(raw)) scores.add(Long.valueOf(score));
            scores.add(Long.valueOf(soldier.getTotalScore()));
            data.add(scores);
        });
        return data;
    }

    public List<List<Long>> getTestGroupScoreData(Long testGroupId, boolean raw) throws InvalidPasscodeException {
        return getTestGroupScoreData(testGroupId, "", raw);
    }

    public Long getPseudoIdQueueSize(){
        return pseudoIdRepository.count();
    }

    public TestGroup getTestGroupByPseudoId(Long pseudoId, String passcode) throws TestGroupNotFoundException{
        List<TestGroup> testgroup = testGroupRepository.findByPseudoId(pseudoId);
        if (testgroup.size() == 0) throw new TestGroupNotFoundException(pseudoId);
        return getTestGroup(testgroup.get(0).getId(), passcode);
    }

    public Soldier getSoldierByPseudoId(Long pseudoId, String passcode){
        Soldier soldier = soldierRepository.findByPseudoId(pseudoId);
        return getSoldierById(soldier.getId(), passcode);
    }

    public Soldier getSoldierByPseudoId(Long pseudoId){
        Soldier soldier = soldierRepository.findByPseudoId(pseudoId);
        return getSoldierById(soldier.getId());
    }

    public boolean pseudoIdRepositoryContains(Long pseudoId){
        boolean result = true;
        List<PseudoId> pseudoIds = pseudoIdRepository.findByPseudoId(pseudoId);
        if (pseudoIds.size() == 0) result = false;
        return result;
    }

    public Long peekPseudoIdRepositoryTop(){
        return pseudoIdRepository.findFirstByOrderByPseudoId().getPseudoId();
    }


}
