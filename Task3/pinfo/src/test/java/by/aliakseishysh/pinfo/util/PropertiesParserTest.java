package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.CliPropertiesException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

public class PropertiesParserTest {

    @Test
    public void parseOptionsTest() throws CliPropertiesException {
        String[] args = new String[] { "-Dtest=test" };
        Properties properties = PropertiesParser.parseOptions(args);
        String expected = "test";
        String actual = (String) properties.get("test");
        Assert.assertEquals(actual, expected);
    }
}
