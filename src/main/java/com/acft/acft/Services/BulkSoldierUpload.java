package com.acft.acft.Services;

import org.springframework.stereotype.Component;

import com.acft.acft.Exceptions.InvalidBulkUploadException;

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
    
    //Still need to implement rejection of improperly formatted input files
    public static List<List<String>> stripBulkSoldierData(File file) throws IOException {
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
        if (values.size() == 0) return values;
        //pop headers and example row off data matrix
        values.remove(0);
        values.remove(0);
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

    public static boolean validateBulkUploadData(List<List<String>> bulkSoldierData) throws InvalidBulkUploadException{
        if (bulkSoldierData.size() < 1 || bulkSoldierData.get(0).size() < 4) throw new InvalidBulkUploadException();
        for (List<String> row : bulkSoldierData){
            for (int i = 0; i < 4; i++){
                switch (i) {
                    case 0:
                        if (row.get(i).length() == 0) throw new InvalidBulkUploadException();
                        break;
                    case 1:
                        if (row.get(i).length() == 0) throw new InvalidBulkUploadException();
                        break;
                    case 2:
                        try {
                            Integer.parseInt(row.get(i));
                        } catch (NumberFormatException e){
                            System.out.println(e.getMessage());
                            throw new InvalidBulkUploadException();
                        }
                        break;
                    case 3:
                        if (!row.get(i).equals("M") && !row.get(i).equals("F") && !row.get(i).equals("m") && !row.get(i).equals("f")) throw new InvalidBulkUploadException();
                        break;
                    default: break;
                }
            }
            
        }
        return true;
    }

}
