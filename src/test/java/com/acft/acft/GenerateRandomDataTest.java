package com.acft.acft;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.acft.acft.Services.GenerateRandomData;

@SpringBootTest
public class GenerateRandomDataTest {

    int size = 20;

    @Autowired
    GenerateRandomData generateRandomData;

    @Test
    void inputStreamNotNull(){
        Assert.notNull(this.generateRandomData.getInputStream(), "In getInputStreamNotNull: input stream was null...");
    }

    @Test
    void constructorGeneratesNameArrayOfCorrectSizeAndContent(){
        List<List<String>> names = generateRandomData.getNames(size);
        Assert.isTrue(names.size() == this.size, "In constructorGeneratesNameArrayOfCorrectSizeAndContent: expected size was " + this.size + ", was " + names.size());
        String expectedName = "Hardin";
        String actualName = names.get(10).get(0);
        Assert.isTrue(actualName.equals(expectedName), "In constructorGeneratesNameArrayOfCorrectSizeAndContent: expected " + expectedName + ", was " + actualName);
    }


}
