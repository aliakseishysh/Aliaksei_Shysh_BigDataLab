package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.PinfoParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

public class PropertiesParserTest {

    @Test
    public void parseOptionsTest() throws PinfoParseException {
        String[] args = new String[] { "-Dtest=test" };
        Properties properties = PropertiesParser.parseOptions(args);
        String expected = "test";
        String actual = (String) properties.get("test");
        Assert.assertEquals(actual, expected);
    }

    @Test(expectedExceptions = { AssertionError.class })
    public void parseOptionsEmptyTest() throws PinfoParseException {
        String[] args = new String[] {  };
        Properties properties = PropertiesParser.parseOptions(args);
        Assert.fail("No AssertionError");
    }

    @Test
    public void parseOptionsNoValueTest() throws PinfoParseException {
        String[] args = new String[] { "-Dtest=" };
        Properties properties = PropertiesParser.parseOptions(args);
        String expected = "";
        String actual = (String) properties.get("test");
        Assert.assertEquals(actual, expected);
    }

    @Test(expectedExceptions = { AssertionError.class })
    public void parseOptionsNullTest() throws PinfoParseException {
        Properties properties = PropertiesParser.parseOptions(null);
        Assert.fail("No AssertionError");
    }
}
