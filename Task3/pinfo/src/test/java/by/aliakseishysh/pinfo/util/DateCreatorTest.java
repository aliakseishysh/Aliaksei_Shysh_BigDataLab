package by.aliakseishysh.pinfo.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;

public class DateCreatorTest {

    @Test
    public void createDatesTest() throws ParseException {
        String[] expected = new String[] { "2020-10", "2020-11", "2020-12", "2021-01" };
        String[] actual = DateCreator.createDates("2020-10", 4).toArray(new String[0]);
        Assert.assertEquals(expected, actual);
    }


}
