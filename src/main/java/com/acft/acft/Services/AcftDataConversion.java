package com.acft.acft.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;



@Service
public class AcftDataConversion {

    private int[][][] scoreTable = new int[6][101][20];

    public AcftDataConversion(){
        try{
            for (int i = 0; i < 6; i++){
                scoreTable[i] = convertSheetValuesToIntMatrix(getSheetValues(i), i);
            }
        } catch (IndexOutOfBoundsException e){
            System.out.println("Index out of bounds for table conversion, all scoreTabe values are now 0");
        }
    }
    
    
    public FileInputStream getFile() {
        String path = "src/main/resources/data/acftScoreTable.xlsx";
        FileInputStream file; 
        try {
            file = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println("In AcftDataConversion 'FileNotFoundException' caught for filepath " + path);
            file = null;
        } 
        return file;
    }

    public Workbook getWorkbook(InputStream file){
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(file);
        } catch (IOException e){
            System.out.println("In AcftDataConversion 'IOException' caught");
            workbook = null;
        }
        return workbook;
    }

    public List<List<String>> getSheetValues(int sheetIndex){
        List<List<String>> sheetValues = new ArrayList<>();
        Workbook workbook = getWorkbook(getFile());
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        for (Row row : sheet){
            List<String> rowArray = new ArrayList<>();
            for (Cell cell : row){
                rowArray.add(getCellValueAsString(cell, sheetIndex));
            }
            sheetValues.add(rowArray);
        }
        return sheetValues;
    }

    public String getCellValueAsString(Cell cell, int sheetIndex){
        String cellValue = "EMPTY";

        switch (cell.getCellType()){
            case NUMERIC:
                if (sheetIndex < 3){
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                }
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

    public static int parseTime(String time){
        if (time.length() < 3) return 0;
        int minutes = 0;
        int seconds = 0;

        String temp = new String();
        for (int i = 0; i < time.length(); i++){
            if (time.charAt(i) == ':'){
                minutes = Integer.parseInt(temp);
                temp = "";
            }
            else temp += time.charAt(i);
        }
        seconds = Integer.parseInt(temp);

        return minutes * 60 + seconds;
    }

    public int convertCellValueToInt(String stringValue, int eventId){
        if (stringValue.equals(new String("---"))) return -1;
        int result;
        switch (eventId){
            case 0:
                result = (int)Float.parseFloat(stringValue);
                break;
            case 1:
                 result = (int)(Float.parseFloat(stringValue) * 10);
                 break;
            case 2:
                result = (int)Float.parseFloat(stringValue);
                break;
            case 3:
                result = parseTime(stringValue);
                break;
            case 4:
                result = parseTime(stringValue);
                break;
            case 5:
                result = parseTime(stringValue);
                break;
            default:
                result = 0;
        }
        return result;
    }

    public int[][] convertSheetValuesToIntMatrix(List<List<String>> sheetValues, int eventId) throws IndexOutOfBoundsException{
        int[][] intMatrix = new int[101][20];
        int rowBound = (eventId == 0 || eventId == 2) ? 49 : 103;
        for (int i = rowBound; i >= 3; i--){
            for (int j = 1; j <= 20; j++){
                int cellValue = convertCellValueToInt(sheetValues.get(i).get(j), eventId);
                if (cellValue == -1) cellValue = intMatrix[i - 2][j - 1];
                intMatrix[i - 3][j - 1] = cellValue;
            }
        }
        return intMatrix;
    }

    public void printTable(int eventId){
        int[][] table = this.scoreTable[eventId];
        for (int[] row : table){
            for (int cell : row) System.out.print(cell + " ");
            System.out.println();
        }
        System.out.println();
    }
    
    //Implement memoization at some point so a long-established server can compute scores quicker
    public int binarySearch(int eventId, int column, int bottom, int top, int target){
        if (bottom > top){
            int mid = (bottom + top) / 2;
            int midVal = this.scoreTable[eventId][mid][column];
            if (midVal == target) return mid;
            if (eventId != 3 && eventId != 5){
                if (midVal < target) return binarySearch(eventId, column, mid - 1, top, target);
                return binarySearch(eventId, column, bottom, mid + 1, target);
            }
            else {
                if (midVal < target) return binarySearch(eventId, column, bottom, mid + 1, target);
                return binarySearch(eventId, column, mid - 1, top, target);
            }
        }
        return bottom;
    }

    public int getScore(int eventId, int rawScore, boolean isMale, int age){
        int ageBracket;
        if (age < 22) ageBracket = 0;
        else if (age > 62) ageBracket = 18;
        else ageBracket = (1 + (age - 22) / 5) * 2;
        int column = (isMale) ? ageBracket : ageBracket + 1;
        int maxScore = scoreTable[eventId][0][column];
        int minScore;
        if (eventId == 0 || eventId == 2){
            minScore = scoreTable[eventId][46][column];
        } else {
            minScore = scoreTable[eventId][100][column];
        }
        boolean lowNumHighScore = (eventId == 3 || eventId == 5) ? true : false;
        if ((!lowNumHighScore && rawScore >= maxScore) || (lowNumHighScore && rawScore <= maxScore)) return 100;
        else if ((!lowNumHighScore && rawScore <= minScore) || (lowNumHighScore && rawScore >= minScore)) return 0;
        int row = binarySearch(eventId, column, 100, 0, rawScore);
        
        if ((!lowNumHighScore && scoreTable[eventId][row][column] > rawScore) || (lowNumHighScore && scoreTable[eventId][row][column] < rawScore)){
            row++;
        }

        while (row < 100 && scoreTable[eventId][row][column] == scoreTable[eventId][row+1][column]) row++;
        int result = 100 - row;
        if ((eventId == 0 || eventId == 2) && result < 60) {
            if (result > 45) result = 0;
            else result = 60 - (60 - result) * 10;
        }
        return result;
    }

    //Utility function for tests
    public static int generateRandomRawScore(int eventId){
        int floor = 0;
        int ceiling = 0;
        switch (eventId){
            case 0:
                floor = 120;
                ceiling = 340;
                break;
            case 1:
                floor = 39;
                ceiling = 130;
                break;
            case 2:
                floor = 10;
                ceiling = 61;
                break;
            case 3:
                floor = 89;
                ceiling = 300;
                break;
            case 4:
                floor = 70;
                ceiling = 220;
                break;
            case 5:
                floor = 780;
                ceiling = 1500;
                break;
            default: break;
        }
        return (int)(Math.random() * (ceiling - floor)) + floor;
    }

}
