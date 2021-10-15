package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.FileWritingException;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {

    private CSVWriter csvWriter;

    public CsvWriter(String filePath) throws FileWritingException {
        try {
            csvWriter = new CSVWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            throw new FileWritingException("Can't initialize " + CSVWriter.class);
        }

    }

    public void writeLine(String[] lineToWrite) {
        try {
            this.csvWriter.writeNext(lineToWrite);
            this.csvWriter.flush();
        } catch (IOException e) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }

}
