package com.acft.acft;

import java.io.FileInputStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class AcftDataConversionTest {
    
    @Autowired
    AcftDataConversion acftDataConversion;


    @Test
    void acftDataConversionCanPullScaleData(){
        FileInputStream file =acftDataConversion.getFile();
        Assert.notNull(file, "acftDataConversion.getFile() returned null");
    }
    
}
