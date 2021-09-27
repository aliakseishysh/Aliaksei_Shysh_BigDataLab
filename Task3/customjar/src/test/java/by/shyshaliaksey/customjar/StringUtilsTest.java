package by.shyshaliaksey.customjar;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Enclosed.class)
public class StringUtilsTest {

    @RunWith(Parameterized.class)
    public static class ParameterizedTest {

        private StringUtils stringUtils = new StringUtils();

        private String number;
        private boolean expected;

        public ParameterizedTest(String number, boolean expected) {
            this.number = number;
            this.expected = expected;
        }

        @Parameterized.Parameters(name = "number {0}, expected {1}")
        public static Collection<Object[]> getNumbers() {
            return Arrays.asList(new Object[][]{
                {"1", true},
                {"-1", false},
                {"0f", false},
                {"zzzzzzzzz", false}
            });
        }

        @Test
        public void validationTest() {
            boolean actual = stringUtils.isPositiveNumber(number);
            assertEquals(expected, actual);
        }
    }
    

}
