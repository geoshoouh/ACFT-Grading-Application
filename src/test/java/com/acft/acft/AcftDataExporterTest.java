package com.acft.acft;

import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.acft.acft.Entities.Soldier;

import com.acft.acft.Services.AcftDataExporter;
import com.acft.acft.Services.AcftManagerService;
import org.springframework.util.Assert;
import java.util.List;

@SpringBootTest
public class AcftDataExporterTest {

    @Autowired
    AcftManagerService acftManagerService;
    
    @Autowired
    AcftDataExporter acftDataExporter;

    

    

    @Test
    void createXlsxWorkbookCreatesWorkbookWithExpectedData(){
        Long testGroupId = acftDataExporter.populateDatabase();
        List<Soldier> soldiers = acftManagerService.getSoldiersByTestGroupId(testGroupId);
        int n = soldiers.size();

        XSSFWorkbook workbook = acftDataExporter.createXlsxWorkbook(testGroupId);
        Assert.isTrue(workbook.getNumberOfSheets() == 2, "In createXlsxWorkbookCreatesWorkbookWithExpectedData: workbook had unexpected number of sheets");
        String[] headerNames = {"ID", "Last", "First", "MDL", "SPT", "HRP", "SDC", "PLK", "2MR", "Total"};
        for (int i = 0; i < workbook.getNumberOfSheets(); i++){
            XSSFSheet sheet = workbook.getSheetAt(i);
            int rowEnd = (i == 0) ? headerNames.length : headerNames.length - 1;
            for (int j = 0; j < rowEnd; j++){
                String cellValue = sheet.getRow(0).getCell(j).getStringCellValue();
                Assert.isTrue(cellValue == headerNames[j], "In createXlsxWorkbookCreatesWorkbookWithExpectedData: unexpected header value");
            }
            for (int j = 0; j < n; j++){
                //offset due to header row having index 0
                Row row = sheet.getRow(j + 1);
                //offset due to ID's starting from 1
                Soldier soldier = acftManagerService.getSoldierById(1L + j);
                Assert.isTrue(Long.valueOf((long)row.getCell(0).getNumericCellValue()) == soldier.getId(), "In createXlsxWorkbookCreatesWorkbookWithExpectedData: unexpected cell value");
                Assert.isTrue(row.getCell(1).getStringCellValue().equals(soldier.getLastName()), "In createXlsxWorkbookCreatesWorkbookWithExpectedData: unexpected cell value");
                Assert.isTrue(row.getCell(2).getStringCellValue().equals(soldier.getFirstName()), "In createXlsxWorkbookCreatesWorkbookWithExpectedData: unexpected cell value");
                for (int k = 3; k < rowEnd; k++){
                    Cell cell = row.getCell(k);
                    String cellValue = AcftDataExporter.getCellValueAsString(cell);
                    String compareValue;
                    if (i == 0){
                        compareValue = (k == rowEnd - 1) ? Integer.toString(soldier.getTotalScore()) : Integer.toString(soldier.getScoreByEventId(k - 3, false));
                    } else compareValue = soldier.getRawScoreAsString(k - 3);
                    System.out.println("cell: " + cellValue + " compare: " + compareValue);
                    Assert.isTrue(cellValue.equals(compareValue), "In createXlsxWorkbookCreatesWorkbookWithExpectedData: unexpected cell value");
                }
            }
        }

    }
}
