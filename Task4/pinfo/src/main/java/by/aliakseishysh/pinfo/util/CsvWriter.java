package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.FileException;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {

    private final CSVWriter csvWriter;

    public CsvWriter(String filePath) throws FileException {
        try {
            csvWriter = new CSVWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            throw new FileException("Can't initialize " + CSVWriter.class);
        }

    }

    /**
     * Writes line to csv file
     *
     * @param lineToWrite array with string to write in 1 line csv
     * @throws FileException if method can't write to file
     */
    public void writeLine(String[] lineToWrite) throws FileException {
        try {
            this.csvWriter.writeNext(lineToWrite);
            this.csvWriter.flush();
        } catch (IOException e) {
            throw new FileException("Can't write to file", e);
        }
    }

    public void close() throws FileException {
        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new FileException("Can't write to file", e);
        }
    }

}
