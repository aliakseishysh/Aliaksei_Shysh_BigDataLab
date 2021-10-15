package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.FileWritingException;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvWriter.class);
    private CSVWriter csvWriter;

    public CsvWriter(String filePath) throws FileWritingException {
        try {
            csvWriter = new CSVWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            throw new FileWritingException("Can't initialize " + CSVWriter.class);
        }

    }

    /**
     * Writes line to csv file
     *
     * @param lineToWrite array with string to write in 1 line csv
     * @throws FileWritingException if method can't write to file
     */
    public void writeLine(String[] lineToWrite) throws FileWritingException {
        try {
            this.csvWriter.writeNext(lineToWrite);
            this.csvWriter.flush();
        } catch (IOException e) {
            LOGGER.error("Can't write to file", e);
            throw new FileWritingException("Can't write to file", e);
        }
    }

}
