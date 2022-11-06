package com.acft.acft.Services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acft.acft.Entities.Soldier;
import com.acft.acft.Entities.TestGroup;


@Service
public class AcftDataExporter {
    
    @Autowired
    AcftManagerService acftManagerService;


    public XSSFWorkbook createXlsxWorkbook(Long testGroupId){
        List<Soldier> soldiers = acftManagerService.getSoldiersByTestGroupId(testGroupId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet scaledSheet = workbook.createSheet("Scaled");
        XSSFSheet rawSheet = workbook.createSheet("Raw");
        Row scaledHeader = scaledSheet.createRow(0);
        Row rawHeader = rawSheet.createRow(0);
        String[] headerNames = {"ID", "Last", "First", "MDL", "SPT", "HRP", "SDC", "PLK", "2MR", "Total"};
        for (int i = 0; i < headerNames.length; i++){
            Cell scaledCell = scaledHeader.createCell(i);
            Cell rawCell = rawHeader.createCell(i);
            scaledCell.setCellValue(headerNames[i]);
            if (i < 9) rawCell.setCellValue(headerNames[i]);
        }
        for (int i = 0; i < soldiers.size(); i++){
            //i is incremented to account for the creation of the header row
            Row scaledRow = scaledSheet.createRow(i+1);
            Row rawRow = rawSheet.createRow(i+1);
            Soldier soldier = soldiers.get(i);
            scaledRow.createCell(0).setCellValue(soldiers.get(i).getId());
            scaledRow.createCell(1).setCellValue(soldiers.get(i).getLastName());
            scaledRow.createCell(2).setCellValue(soldiers.get(i).getFirstName());
            rawRow.createCell(0).setCellValue(soldiers.get(i).getId());
            rawRow.createCell(1).setCellValue(soldiers.get(i).getLastName());
            rawRow.createCell(2).setCellValue(soldiers.get(i).getFirstName());
            for (int j = 0; j < 6; j++){
                scaledRow.createCell(j + 3).setCellValue(soldier.getScoreByEventId(j, false));
                rawRow.createCell(j + 3).setCellValue(soldier.getRawScoreAsString(j));
            }
            scaledRow.createCell(9).setCellValue(soldier.getTotalScore());
        }
        return workbook;
    }

    public FileOutputStream createXlsxFile(XSSFWorkbook workbook, Long testGroupId){
        String path = "src/main/resources/data/testGroup_" + testGroupId + ".xlsx";
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
            workbook.write(fileOutputStream);
        } catch (FileNotFoundException e){
            System.out.println("File not found in path " + path);
        } catch (IOException e){
            System.out.println("Exception thrown for write to file at path " + path);
        }
        return fileOutputStream;
    }

    //Utility function for tests; returns testGroupId for populated group
    public Long populateDatabase(){
        Long testGroupId = acftManagerService.createNewTestGroup();
        TestGroup testGroup = acftManagerService.getTestGroup(testGroupId, "");
        int n = 5;
        Long[] soldierIds = new Long[5];
        String[] lastNames = {"Smith", "Jones", "Samuels", "Smith", "Conway"};
        String[] firstNames = {"Jeff", "Timothy", "Darnell", "Fredrick", "Katherine"};
        int[] ages = {26, 18, 19, 30, 23};
        boolean[] genders = {true, true, true, true, false};
        for (int i = 0; i < n; i++){
            soldierIds[i] = acftManagerService.createNewSoldier(testGroup, lastNames[i], firstNames[i], ages[i], genders[i]);
            for (int j = 0; j < 6; j++){
                acftManagerService.updateSoldierScore(soldierIds[i], j, AcftDataConversion.generateRandomRawScore(j));
            }
        }
        return testGroupId;
    }

    public static String getCellValueAsString(Cell cell){
        String cellValue = "EMPTY";
        switch (cell.getCellType()){
            case NUMERIC:
                cellValue = Integer.toString((int)cell.getNumericCellValue());
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case BOOLEAN: 
                cellValue = (cell.getBooleanCellValue()) ? "true" : "false";
                break;
            case FORMULA: 
                cellValue = cell.getCellFormula();
                break;
            case BLANK: 
                break;
            case ERROR: 
                cellValue = "ERROR";
                break;
            case _NONE: 
                break;
            default: break;
        }
        return cellValue;
    }

}
