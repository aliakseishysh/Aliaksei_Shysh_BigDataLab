package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.FileReadingException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvReader.class);

    /**
     * Reads latitude and longitude from file
     *
     * @param filePath path to file
     * @return {@code List<String[]>} with latitude and longitude
     * @throws FileReadingException if method can't read file
     */
    public static List<String[]> readLines(String filePath) throws FileReadingException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.skip(1);
            return reader.readAll();
        } catch (IOException | CsvException e) {
            LOGGER.error("Can't read csv file", e);
            throw new FileReadingException("Can't read csv file", e);
        }
    }

}
