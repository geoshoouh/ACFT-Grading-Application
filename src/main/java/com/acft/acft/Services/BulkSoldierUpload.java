package com.acft.acft.Services;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

@Component
public class BulkSoldierUpload {
    
    public static List<List<String>> stripBulkSoldierData(File file) throws IOException{
        List<List<String>> values = new ArrayList<>();
        Workbook workbook = AcftDataConversion.getWorkbook(new FileInputStream(file));
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet){
            List<String> rowArray = new ArrayList<>();
            for (Cell cell : row){
                rowArray.add(getCellValueAsString(cell));
            }
            values.add(rowArray);
        }
        return values;
    }

    
    public static String getCellValueAsString(Cell cell){
        String cellValue = "EMPTY";
        switch (cell.getCellType()){
            case NUMERIC:
                String temp = cell.getDateCellValue().toString();
                String[] tempArray = temp.split(" ");
                cellValue = tempArray[3];
                cellValue = cellValue.substring(0, 5);
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
            case BOOLEAN: break;
            case FORMULA: break;
            case BLANK: break;
            case ERROR: break;
            case _NONE: break;
        }
        return cellValue;
    }

}
