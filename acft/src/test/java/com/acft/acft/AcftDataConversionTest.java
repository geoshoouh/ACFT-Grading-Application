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
        int eventId = 5;
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
        int correctScore = 89;
        int score = acftDataConversion.getScore(1, 110, true, 31);
        Assert.isTrue(score == correctScore, "score from acftDataConversion.getScore() was supposed to be " + correctScore + " but was " + score);

        //Test exceptional case below 60, which should be different for MDL and SPT
        int correctScore_2 = 0;
        int score_2 = acftDataConversion.getScore(0, 95, false, 25);
        Assert.isTrue(score_2 == correctScore_2, "score from acftDataConversion.getScore() was supposed to be " + correctScore_2 + " but was " + score_2);

        int correctScore_3 = 0;
        int score_3 = acftDataConversion.getScore(0, 0, true, 19);
        Assert.isTrue(score_3 == correctScore_3, "score from acftDataConversion.getScore() was supposed to be " + correctScore_3 + " but was " + score_3);

        int correctScore_4 = 84;
        int score_4 = acftDataConversion.getScore(1, 101, true, 26);
        Assert.isTrue(score_4 == correctScore_4, "score from acftDataConversion.getScore() was supposed to be " + correctScore_4 + " but was " + score_4);

        int correctScore_5 = 85;
        int score_5 = acftDataConversion.getScore(3, 147, false, 33);
        Assert.isTrue(score_5 == correctScore_5, "score from acftDataConversion.getScore() was supposed to be " + correctScore_5 + " but was " + score_5);
        
        int correctScore_6 = 100;
        int score_6 = acftDataConversion.getScore(5, 120, false, 102);
        Assert.isTrue(score_6 == correctScore_6, "score from acftDataConversion.getScore() was supposed to be " + correctScore_6 + " but was " + score_6);
    }
}
