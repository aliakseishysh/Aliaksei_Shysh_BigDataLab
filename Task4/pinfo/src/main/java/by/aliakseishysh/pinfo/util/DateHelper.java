package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.PinfoParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static by.aliakseishysh.pinfo.dao.DatabaseColumn.SAS_DATETIME;

public class DateHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateHelper.class);
    private static final SimpleDateFormat[] formats = new SimpleDateFormat[] {
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
    };

    /**
     * Creates list of dates.
     * @param startDate start date
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
        for (int i = 0; i < formats.length; i++) {
            try {
                return formats[i].parse(stringDate);
            } catch (ParseException e) {
                // LOGGER.info("Can't parse", e);
            }
        }
        throw new PinfoParseException("Can't parse date: " + stringDate);
    }


}
