package com.acft.acft;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.acft.acft.Services.BulkSoldierUpload;

@SpringBootTest
public class BulkSoldierUploadTest {

    @Autowired
    BulkSoldierUpload bulkSoldierUpload;
    
    @Test
    void generateBulkUploadTestFile(){
        String path = "src/main/resources/data/bulkUploadTest.xlsx";
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row header = sheet.createRow(0);
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(path);
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
        
}
