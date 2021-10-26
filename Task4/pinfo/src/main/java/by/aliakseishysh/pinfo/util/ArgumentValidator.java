package by.aliakseishysh.pinfo.util;

import java.util.regex.Pattern;

public class ArgumentValidator {

    private static final String CORRECT_DATE = "^\\d{4}-\\d{2}$";
    private static final String CORRECT_MONTH = "^[1-9][0-9]{0,5}$";

    private ArgumentValidator() {
    }

    public static boolean validateDate(String date) {
        return date != null && Pattern.matches(CORRECT_DATE, date);
    }

    public static boolean validateMonthCount(String monthCount) {
        return monthCount != null && Pattern.matches(CORRECT_MONTH, monthCount);
    }

    public static boolean validateCsvFilePath(String filePath) {
        return filePath != null && filePath.length() > 0;
    }

    public static boolean validateSaveToFilePath(String filePath) {
        return filePath != null && filePath.length() > 0;
    }

}
