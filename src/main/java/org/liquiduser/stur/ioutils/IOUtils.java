package org.liquiduser.stur.ioutils;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    public static String readBufferedReader(BufferedReader reader) throws IOException{
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            result.append(line + "\n");
        }
        return result.toString();
    }
}