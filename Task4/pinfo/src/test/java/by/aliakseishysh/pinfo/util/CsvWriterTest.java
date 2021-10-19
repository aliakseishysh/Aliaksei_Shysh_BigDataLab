package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.FileException;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.fail;

public class CsvWriterTest {

    private static final String correctFilePath = "/aliaksei/Aliaksei_Shysh_BigDataLab/Task3/tests/CsvWriterFile.file";

    @Test
    public void writeLineTest() throws FileException {
        CsvWriter csvWriter = new CsvWriter(correctFilePath);
        csvWriter.writeLine(new String[]{"sdfsdf", "xcvxcv", "asdasa a sd a DASD * #(*,,,"});
        File file = new File(correctFilePath);
        if (!file.exists()) {
            fail("File not exists: " + correctFilePath);
        }
        csvWriter.close();
        file.delete();
    }

}
