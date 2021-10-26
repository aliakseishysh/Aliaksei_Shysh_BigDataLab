package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.FileException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvReader {

    private static final int LINES_TO_SKIP_COUNT = 1;

    /**
     * Reads latitude and longitude from file
     *
     * @param filePath path to file
     * @return {@code List<String[]>} with latitude and longitude
     * @throws FileException if method can't read file
     */
    public static List<String[]> readLines(String filePath) throws FileException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.skip(LINES_TO_SKIP_COUNT);
            return reader.readAll();
        } catch (IOException | CsvException | NullPointerException e) {
            throw new FileException("Can't read csv file: " + e.getCause());
        }
    }

}
