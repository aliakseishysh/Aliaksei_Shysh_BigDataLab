package by.aliakseishysh.pinfo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class DateCreator {

    public static List<String> createDates(String startDate, int monthCount) throws ParseException {
        List<String> dates = new ArrayList<>();
        dates.add(startDate);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(startDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        IntStream.range(1, monthCount)
                .forEach((index) -> {
                    calendar.add(Calendar.MONTH, 1);
                    dates.add(sdf.format(calendar.getTime()));
                });
        return dates;
    }


}
