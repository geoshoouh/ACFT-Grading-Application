package com.acft.acft.Services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.acft.acft.Entities.Soldier;


@Component
public class AcftDataExporter {
    
    public XSSFWorkbook createXlsxWorkbook(List<Soldier> soldiers){
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet scaledSheet = workbook.createSheet("Scaled");
        XSSFSheet rawSheet = workbook.createSheet("Raw");
        Row scaledHeader = scaledSheet.createRow(0);
        Row rawHeader = rawSheet.createRow(0);
        String[] headerNames = {"ID", "Last", "First", "Age", "Gender", "MDL", "SPT", "HRP", "SDC", "PLK", "2MR", "Total"};
        for (int i = 0; i < headerNames.length; i++){
            Cell scaledCell = scaledHeader.createCell(i);
            Cell rawCell = rawHeader.createCell(i);
            scaledCell.setCellValue(headerNames[i]);
            if (i < 11) rawCell.setCellValue(headerNames[i]);
        }
        for (int i = 0; i < soldiers.size(); i++){
            //i is incremented to account for the creation of the header row
            Row scaledRow = scaledSheet.createRow(i+1);
            Row rawRow = rawSheet.createRow(i+1);
            Soldier soldier = soldiers.get(i);
            scaledRow.createCell(0).setCellValue(soldier.getPseudoId());
            scaledRow.createCell(1).setCellValue(soldier.getLastName());
            scaledRow.createCell(2).setCellValue(soldier.getFirstName());
            scaledRow.createCell(3).setCellValue(soldier.getAge());
            scaledRow.createCell(4).setCellValue((soldier.isMale()) ? "M" : "F");
            rawRow.createCell(0).setCellValue(soldier.getPseudoId());
            rawRow.createCell(1).setCellValue(soldier.getLastName());
            rawRow.createCell(2).setCellValue(soldier.getFirstName());
            rawRow.createCell(3).setCellValue(soldier.getAge());
            rawRow.createCell(4).setCellValue((soldier.isMale()) ? "M" : "F");
            for (int j = 0; j < 6; j++){
                scaledRow.createCell(j + 5).setCellValue(soldier.getScoreByEventId(j, false));
                rawRow.createCell(j + 5).setCellValue(soldier.getRawScoreAsString(j));
            }
            scaledRow.createCell(11).setCellValue(soldier.getTotalScore());
        }
        return workbook;
    }

    public String createXlsxFile(XSSFWorkbook workbook, Long testGroupId){
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
        return path;
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
