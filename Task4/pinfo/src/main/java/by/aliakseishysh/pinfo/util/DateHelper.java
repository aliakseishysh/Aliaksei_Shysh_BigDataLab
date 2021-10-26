package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.PinfoParseException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class DateHelper {

    private static final SimpleDateFormat[] formats = new SimpleDateFormat[]{
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
    };

    /**
     * Creates list of dates.
     *
     * @param startDate  start date
     * @param monthCount month amount to create (including {@code startDate})
     * @return list of created dates
     * @throws ParseException if method can't parse {@code startDate}
     */
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

    public static Date parseDate(String stringDate) throws PinfoParseException {
        for (SimpleDateFormat format : formats) {
            try {
                return format.parse(stringDate);
            } catch (ParseException e) {
                // nothing to do here
            }
        }
        throw new PinfoParseException("Can't parse date: " + stringDate);
    }


}
