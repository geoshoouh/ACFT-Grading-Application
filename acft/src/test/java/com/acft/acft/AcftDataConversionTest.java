package com.acft.acft;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class AcftDataConversionTest {
    
    @Autowired
    AcftDataConversion acftDataConversion;

    @Test
    void getSheetValuesShouldReturnNonEmptyMatrix(){
        List<List<String>> sheetValues = acftDataConversion.getSheetValues(0);
        for (List<String> row : sheetValues){
            for (String item : row){
                System.out.print(item + " ");
            }
            System.out.println();
        }
        Assert.notNull(sheetValues, "Matrix returned by acftDataConversion.getSheetValues was empty");
    }
    
    @Test
    void parseTimePropertyDerivesTotalSeconds(){
        Assert.isTrue(AcftDataConversion.parseTime("2:31") == 2 * 60 + 31, "AcftDataConversion.parseTime returned incorrect value");
        Assert.isTrue(AcftDataConversion.parseTime("18:53") == 18 * 60 + 53, "AcftDataConversion.parseTime returned incorrect value");
    }

    //Prints resulting matrix, no assertions
    @Test
    void convertSheetValuesToIntMatrixShouldReturnIntMatrix(){
        int eventId = 0;
        List<List<String>> sheetValues = acftDataConversion.getSheetValues(eventId);
        int[][] intMatrix = acftDataConversion.convertSheetValuesToIntMatrix(sheetValues, eventId);
        for (int[] row : intMatrix){
            for (int cell : row){
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }


    //Prints resulting matrix, no assertions
    @Test
    void AcftDataConversionConstructorCreatesScoreChart(){
        for (int i = 0; i < 6; i++){
            acftDataConversion.printTable(i);
        }
    }

    @Test
    void getScoreReturnsProperScore(){

        //Test typical case above 60 points
        int correctScore = 87;
        int score = acftDataConversion.getScore(0, 165, false, 25);
        Assert.isTrue(score == correctScore, "score from acftDataConversion.getScore() was supposed to be " + correctScore + " but was " + score);

        //Test exceptional case below 60, which should be different for MDL and SPT
        int correctScore_2 = 10;
        int score_2 = acftDataConversion.getScore(0, 75, false, 25);
        Assert.isTrue(score_2 == correctScore_2, "score from acftDataConversion.getScore() was supposed to be " + correctScore_2 + " but was " + score_2);
    }
}
