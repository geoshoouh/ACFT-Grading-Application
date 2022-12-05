package com.acft.acft.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class GenerateRandomData {
    
    private String filepath = "src/main/resources/data/names.txt";

    private InputStream inputStream = generateInputStream();

    private InputStream generateInputStream(){
        try {
            InputStream inputStream = new FileInputStream(new File(this.filepath));
            return inputStream;
        } catch (FileNotFoundException e) {
            System.out.println("In AcftDataConversion 'FileNotFoundException' caught for filepath " + filepath);
            return null;
        } 
    }

    public List<List<String>> getNames(int size){
        List<List<String>> names = new ArrayList<>();
        String body;
        try {
            body = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name()); 
        } catch (IOException e) {
            System.out.println(e);
            return names;
        }
        List<String> lastFirst= Arrays.asList(new String[2]);
        StringBuffer name = new StringBuffer();
        for (int i = 0; i < body.length(); i++){
            if (body.charAt(i) == ' '){
                lastFirst.get(1);
                lastFirst.set(1, name.toString());
                name.setLength(0);
            }
            else if (body.charAt(i) == '\n'){
                lastFirst.set(0, name.toString());
                names.add(lastFirst);
                name.setLength(0);
                lastFirst = Arrays.asList(new String[2]);
                if (names.size() == size) break;
            }
            else if (i == body.length() - 1){
                name.append(body.charAt(i));
                lastFirst.set(0, name.toString());
                names.add(lastFirst);
            }
            else {
                name.append(body.charAt(i));
            }
        }
    
        return names;
    }

    public InputStream getInputStream(){
        return this.inputStream;
    }

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

    public static int generateRandomAge(){
        int floor = 17;
        int ceiling = 63;
        return (int)Math.floor(Math.random() * (ceiling - floor)) + floor;
    }

    public static boolean generateRandomGender(){
        return (Math.floor(Math.random() * 2) == 1) ? true : false;
    }


}
