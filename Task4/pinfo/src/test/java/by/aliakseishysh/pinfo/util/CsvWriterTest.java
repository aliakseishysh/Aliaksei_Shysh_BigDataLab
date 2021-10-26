package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.FileException;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.testng.Assert.fail;

public class CsvWriterTest {

    private static final String CORRECT_FILE_PATH = "/aliaksei/Aliaksei_Shysh_BigDataLab/Task4/tests/CsvWriterFile.file";
    private static final String INCORRECT_FILE_PATH = "/aliaksei/Aliaksei_Shysh_BigDataLab/Task4/tests//|\\";

    @Test
    public void writeLineTest() throws FileException, IOException {
        CsvWriter csvWriter = new CsvWriter(CORRECT_FILE_PATH);
        csvWriter.writeLine(new String[]{"sdfsdf", "xcvxcv", "asdasa a sd a DASD * #(*,,,"});
        File file = new File(CORRECT_FILE_PATH);
        BufferedReader br = new BufferedReader(new FileReader(CORRECT_FILE_PATH));
        if (!file.exists()) {
            fail("File not exists: " + CORRECT_FILE_PATH);
        }
        if (!br.readLine().equals("\"sdfsdf\",\"xcvxcv\",\"asdasa a sd a DASD * #(*,,,\"")) {
            fail("Incorrect line: " + CORRECT_FILE_PATH);
        }
        br.close();
        csvWriter.close();
        file.delete();
    }

    @Test(expectedExceptions = {FileException.class})
    public void writeLineIncorrectPathTest() throws FileException {
        CsvWriter csvWriter = new CsvWriter(INCORRECT_FILE_PATH);
        fail("Path is correct: " + INCORRECT_FILE_PATH);
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void writeLineNullPathTest() throws FileException {
        CsvWriter csvWriter = new CsvWriter(null);
        fail("Path is correct: " + INCORRECT_FILE_PATH);
    }

}
