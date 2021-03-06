package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.dao.impl.AllCrimesDaoFileImpl;
import by.aliakseishysh.pinfo.dao.impl.AllCrimesDaoImpl;
import by.aliakseishysh.pinfo.exception.CommandException;
import by.aliakseishysh.pinfo.exception.FileException;
import by.aliakseishysh.pinfo.util.ArgumentValidator;
import by.aliakseishysh.pinfo.util.CsvReader;
import by.aliakseishysh.pinfo.util.CsvWriter;
import by.aliakseishysh.pinfo.util.DataDownloader;
import by.aliakseishysh.pinfo.util.DateHelper;
import by.aliakseishysh.pinfo.util.NameValuePairBuilder;
import by.aliakseishysh.pinfo.util.ResponseParser;
import by.aliakseishysh.pinfo.util.UriBuilder;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Command for downloading data from all-crime api.
 */
public class AllCrimeCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllCrimeCommand.class);
    private static final int LOG_AFTER_ADD_AMOUNT = 100;

    /**
     * Downloading and processing data from api.
     *
     * @param properties command line arguments
     * @throws CommandException if method can't execute the command
     */
    public void execute(final Properties properties) throws CommandException {
        try {
            String coordinatesPath = properties.getProperty(Argument.FILE_PATH.name().toLowerCase());
            String date = properties.getProperty(Argument.DATE.name().toLowerCase());
            String monthCountString = properties.getProperty(Argument.MONTH_COUNT.name().toLowerCase());

            String saveToFile = properties.getProperty(Argument.SAVE_TO_FILE.name().toLowerCase());
            if (!ArgumentValidator.validateCsvFilePath(coordinatesPath) || !ArgumentValidator.validateDate(date)
                || !ArgumentValidator.validateMonthCount(monthCountString)) {
                LOGGER.error("Can't execute the command: property is not valid");
                throw new CommandException("Can't execute the command: property is not valid");
            } else {
                int monthCount = Integer.parseInt(monthCountString);
                List<String> dates = DateHelper.createDates(date, monthCount);
                Queue<String> requestUris = createRequests(coordinatesPath, dates);

                List<String> responses = new DataDownloader().downloadAll(requestUris);
                Queue<Map<String, Object>> allCrimeResponseObjects = new LinkedList<>();
                PoliceApiDao policeDao;
                AtomicInteger counter = new AtomicInteger(0);
                AtomicInteger index = new AtomicInteger(0);

                long time1 = System.currentTimeMillis();
                if (ArgumentValidator.validateSaveToFilePath(saveToFile)) {
                    CsvWriter csvWriter = new CsvWriter(saveToFile);
                    policeDao = new AllCrimesDaoFileImpl(false, csvWriter);
                } else {
                    policeDao = AllCrimesDaoImpl.getInstance();
                }
                responses.forEach((rsp) -> allCrimeResponseObjects.addAll(ResponseParser.parseAllCrimeResponse(rsp)));

                while  (!allCrimeResponseObjects.isEmpty()) {
                    boolean result = policeDao.add(allCrimeResponseObjects.poll());
                    if (result) {
                        int localIndex = index.get();
                        if (localIndex >= LOG_AFTER_ADD_AMOUNT) {
                            LOGGER.info(counter.get() + " objects added to database; objects remaining: " + allCrimeResponseObjects.size());
                            index.set(0);
                        }
                        index.incrementAndGet();
                        counter.incrementAndGet();
                    }
                }
                policeDao.clear();
                long time2 = System.currentTimeMillis();
                LOGGER.info(counter.get() + " objects added to database; consumed time: " + (time2 - time1));
            }
        } catch (ParseException | NumberFormatException | FileException e) {
            LOGGER.error("Can't execute the command", e);
            throw new CommandException("Can't execute the command", e);
        }

    }

    /**
     * Creates query uris for current command.
     *
     * @param coordinatesPath path to file with coordinates
     * @param dates           download data on this dates
     * @return queue with uris for current command
     * @throws FileException if method can't read file with coordinates
     */
    private Queue<String> createRequests(final String coordinatesPath, List<String> dates) throws FileException {
        List<String[]> places = CsvReader.readLines(coordinatesPath);
        Queue<String> requestUris = new LinkedList<>();
        dates.forEach((date) -> places.forEach((place) -> {
            String csvLng = place[1];
            String csvLat = place[2];
            List<NameValuePair> pairs = NameValuePairBuilder.newBuilder()
                    .addPair(Argument.DATE.name().toLowerCase(), date)
                    .addPair(Argument.LAT.name().toLowerCase(), csvLat)
                    .addPair(Argument.LNG.name().toLowerCase(), csvLng)
                    .build();
            requestUris.add(UriBuilder.buildUri(PoliceApi.ALL_CRIME, pairs));
        }));

        return requestUris;
    }


}
