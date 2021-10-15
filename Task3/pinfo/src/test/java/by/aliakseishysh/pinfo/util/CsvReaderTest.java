package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.FileException;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class CsvReaderTest {

    // if test don't pass, please, check your file first
    private static final String correctFilePath = "/aliaksei/Aliaksei_Shysh_BigDataLab/Task3/tests/LondonStations.csv.old";
    private static final String badFilePath = "/aliaksei/Aliaksei_Shysh_BigDataLab/Task3/tests/LondonStations.csv.olddddd";

    @Test
    public void readLinesSizeTest() throws FileException {
        List<String[]> strings = CsvReader.readLines(correctFilePath);
        assertEquals(strings.size(), 641);
    }

    @Test (expectedExceptions = { FileException.class })
    public void readLinesNullTest() throws FileException {
        CsvReader.readLines(null);
        fail("No " + FileException.class + " occurred");
    }

    @Test (expectedExceptions = { FileException.class })
    public void readLinesNoFileTest() throws FileException {
        CsvReader.readLines(badFilePath);
        fail("No " + FileException.class + " occurred");
    }

}
