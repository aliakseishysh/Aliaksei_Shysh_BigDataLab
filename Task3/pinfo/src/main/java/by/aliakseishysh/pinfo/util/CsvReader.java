package by.aliakseishysh.pinfo.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvReader {

    public static List<String[]> readLines(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.skip(1);
            List<String[]> lines = reader.readAll();
            return lines;
        } catch (FileNotFoundException e) {
            throw new UnsupportedOperationException(); // TODO handle exception
        } catch (IOException e) {
            throw new UnsupportedOperationException(); // TODO handle exception
        } catch (CsvException e) {
            throw new UnsupportedOperationException(); // TODO handle exception
        }
    }

}
