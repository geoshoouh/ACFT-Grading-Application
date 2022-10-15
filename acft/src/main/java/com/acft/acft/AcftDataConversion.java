package com.acft.acft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class AcftDataConversion {

    private FileInputStream file;
    private Workbook workbook;

    
    private HashMap<Integer, List<List<Integer>>> MDL;
    /* 
    private HashMap<Integer, List<List<Integer>>> SPT;
    private HashMap<Integer, List<List<Integer>>> HRP;
    private HashMap<Integer, List<List<Integer>>> SDC;
    private HashMap<Integer, List<List<Integer>>> PLK;
    private HashMap<Integer, List<List<Integer>>> TMR;
    */

    public AcftDataConversion(){
        
        
    }
    
    public FileInputStream getFile() {
        FileInputStream file;
        String path = "/Users/joshuatate/Documents/GitHub/ACFT-Grading-Application/acft/src/main/data/acftScoreTable.xlsx";
        try {
            file = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println("In AcftDataConversion 'FileNotFoundException' caught for filepath " + path);
            file = null;
        }
        return file;
    }



}
