package com.acft.acft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.acft.acft.Exceptions.InvalidBulkUploadException;
import com.acft.acft.Services.BulkSoldierUpload;
import com.acft.acft.Services.GenerateRandomData;

@SpringBootTest
public class BulkSoldierUploadTest {

    //GenerateRandomData generateRandomData = new GenerateRandomData();

    String testPath = "src/main/resources/data/bulkUploadTest.xlsx";

    void generateBulkUploadTestFile(int n) {
        GenerateRandomData generateRandomData = new GenerateRandomData();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        List<List<String>> soldierNames = generateRandomData.getNames(n);
        Row header = sheet.createRow(0);
        Row example = sheet.createRow(1);
        //Out-of-bounds errors thrown from these rows not being created, so I created them
        for (int i = 0; i < 4; i++){
            header.createCell(i);
            example.createCell(i);
        }
        for (int i = 0; i < n; i++){
            Row currentRow = sheet.createRow(2 + i);
            currentRow.createCell(0).setCellValue(soldierNames.get(i).get(0));
            currentRow.createCell(1).setCellValue(soldierNames.get(i).get(1));
            currentRow.createCell(2).setCellValue(String.valueOf(GenerateRandomData.generateRandomAge()));
            currentRow.createCell(3).setCellValue((GenerateRandomData.generateRandomGender()) ? "M" : "F");
        }
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(testPath);
            workbook.write(outputStream);
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        try {
            workbook.close();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
        
    void printStrippedData(List<List<String>> data){
        for (List<String> row : data){
            row.forEach((element) -> {
                System.out.print(element + " ");
            });
            System.out.println();
        }
    }

    @Test
    void stripBulkSoldierDataGetsStringMatrix() throws Exception {
        int sz = 5;
        generateBulkUploadTestFile(sz);
        List<List<String>> bulkUploadData = new ArrayList<>();
        File file = new File(testPath);
        bulkUploadData = BulkSoldierUpload.stripBulkSoldierData(file);
        Assert.notNull(bulkUploadData, "In stripBulkSoldierDataGetsStringMatrix: result of stripBulkSoldierData was null");
        printStrippedData(bulkUploadData);
        file.delete();
    }

    @Test
    void validateBulkUploadCorrectlyValidatesUpload() throws Exception {
        int sz = 5;
        generateBulkUploadTestFile(sz);
        File file = new File(testPath);
        String exceptionMessage = "In validateBulkUploadCorrectlyValidatesUpload: unexpected validation result";
        List<List<String>> bulkUploadData = new ArrayList<>();
        bulkUploadData = BulkSoldierUpload.stripBulkSoldierData(file);
        boolean validationResult = BulkSoldierUpload.validateBulkUploadData(bulkUploadData);
        Assert.isTrue(validationResult, exceptionMessage);
        InputStream inputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(2);
        int n = 4;
        int exceptionCount = 0;
        for (int i = 0; i < n; i++){
            Cell cell = row.getCell(i);
            String originalCellValue = cell.getStringCellValue();
            //System.out.println("Original Value: " + cell.getStringCellValue());
            switch (i){
                case 0:
                    cell.setCellValue("");
                    break;
                case 1:
                    cell.setCellValue("");
                    break;
                case 2:
                    cell.setCellValue("hola");
                    break;
                case 3:
                    cell.setCellValue("Q");
                    break;
            }
            //System.out.println("Altered Value: " + cell.getStringCellValue());
            OutputStream outputStream = new FileOutputStream(testPath);
            workbook.write(outputStream);
            try {
                BulkSoldierUpload.validateBulkUploadData(BulkSoldierUpload.stripBulkSoldierData(file));
            } catch (InvalidBulkUploadException e){
                exceptionCount++;
            }
            //Reset value to ensure test is catching error in each cell
            cell.setCellValue(originalCellValue);
            workbook.write(outputStream);
        }
        Assert.isTrue(exceptionCount == n, exceptionMessage);
        workbook.close();
        file.delete();
    }
}
