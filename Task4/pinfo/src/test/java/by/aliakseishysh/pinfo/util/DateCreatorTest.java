package by.aliakseishysh.pinfo.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;

public class DateCreatorTest {

    @Test
    public void createDatesTest() throws ParseException {
        String[] expected = new String[] { "2020-10", "2020-11", "2020-12", "2021-01" };
        String[] actual = DateHelper.createDates("2020-10", 4).toArray(new String[0]);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void createDatesIncorrectTest() throws ParseException {
        String[] expected = new String[] { "2000-10", "2020-11", "2020-12", "2021-02" };
        String[] actual = DateHelper.createDates("2020-10", 4).toArray(new String[0]);
        Assert.assertNotEquals(expected, actual);
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void createDatesNullTest() throws ParseException {
        String[] expected = new String[] { "2000-10", "2020-11", "2020-12", "2021-02" };
        String[] actual = DateHelper.createDates(null, 4).toArray(new String[0]);
        Assert.assertNotEquals(expected, actual);
    }


}
