package by.shyshaliaksey.core;

import java.util.Arrays;
import java.util.List;

import by.shyshaliaksey.customjar.StringUtils;

public class Utils {
    
    private static final StringUtils stringUtils = new StringUtils();

    public boolean isAllPositiveNumbers(String ... str) {
        List<String> numbers = Arrays.asList(str);
        return numbers.stream().allMatch(stringUtils::isPositiveNumber);
    }

}
