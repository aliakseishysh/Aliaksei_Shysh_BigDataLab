package by.aliakseishysh.pinfo.util;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ArgumentValidatorTest {

    @DataProvider(name = "dateDataProvider")
    public Object[][] dateDataProvider() {
        return new Object[][]{
                {"0000-00", true},
                {"2021-01", true},
                {"2021-12-12", false},
                {"January", false},
                {"", false},
                {null, false}
        };
    }

    @DataProvider(name = "monthCountProvider")
    public Object[][] monthCountProvider() {
        return new Object[][]{
                {"0", false},
                {"00", false},
                {"10", true},
                {null, false},
                {"", false},
                {"11111", true},
                {"1111111", false}
        };
    }

    @Test(dataProvider = "dateDataProvider")
    public void dataTest(String data, Boolean expected) {
        Boolean actual = ArgumentValidator.validateDate(data);
        Assert.assertEquals(actual, expected);
    }

    @Test(dataProvider = "monthCountProvider")
    public void monthCountTest(String data, Boolean expected) {
        Boolean actual = ArgumentValidator.validateMonthCount(data);
        Assert.assertEquals(actual, expected);
    }


}
