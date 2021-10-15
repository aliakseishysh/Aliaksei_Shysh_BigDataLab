package by.aliakseishysh.pinfo.util;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {

    public static void writeCsvAllCrime(String filePath, String[] lineToWrite) {
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath, true));
            csvWriter.writeNext(lineToWrite);
            csvWriter.flush();
        } catch (IOException e) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }

}
