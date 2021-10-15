package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.dao.impl.PoliceApiDaoFileImpl;
import by.aliakseishysh.pinfo.dao.impl.PoliceApiDaoImpl;
import by.aliakseishysh.pinfo.exception.CommandException;
import by.aliakseishysh.pinfo.exception.FileReadingException;
import by.aliakseishysh.pinfo.exception.FileWritingException;
import by.aliakseishysh.pinfo.util.CsvReader;
import by.aliakseishysh.pinfo.util.CsvWriter;
import by.aliakseishysh.pinfo.util.DataDownloader;
import by.aliakseishysh.pinfo.util.DateCreator;
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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Command for downloading data from all-crime api.
 */
public class AllCrimeCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllCrimeCommand.class);

    // TODO add option "save to file"

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
            // TODO parse int handle
            String monthCountString = properties.getProperty(Argument.MONTH_COUNT.name().toLowerCase());
            int monthCount = !monthCountString.equals("") ? Integer.parseInt(monthCountString) : 1;
            String saveToFile = properties.getProperty(Argument.SAVE_TO_FILE.name().toLowerCase());
            if (coordinatesPath != "" && date != "") {
                List<String> dates = DateCreator.createDates(date, monthCount);
                Queue<String> requestUris = createRequests(coordinatesPath, dates);

                List<String> responses = new DataDownloader().downloadAll(requestUris);
                ConcurrentLinkedQueue<Map<String, Object>> allCrimeResponseObjects = new ConcurrentLinkedQueue<>();
                PoliceApiDao policeDao;
                AtomicInteger counter = new AtomicInteger(1);
                AtomicInteger index = new AtomicInteger(1);

                long time1 = System.currentTimeMillis();
                if (saveToFile != null)  {
                    CsvWriter csvWriter = new CsvWriter(saveToFile);
                    policeDao = new PoliceApiDaoFileImpl(false, csvWriter);
                } else {
                    policeDao = PoliceApiDaoImpl.getInstance();
                }

                responses.forEach((rsp) -> allCrimeResponseObjects.addAll(ResponseParser.parse(rsp)));
                allCrimeResponseObjects.forEach((obj) -> {
                    policeDao.add(obj);
                    int localIndex = index.get();
                    if (localIndex >= 100) {
                        LOGGER.info(counter.get() + " objects added to database");
                        index.set(1);
                    }
                    index.incrementAndGet();
                    counter.incrementAndGet();
                });
                policeDao.clear();

                long time2 = System.currentTimeMillis();
                LOGGER.info(counter.get() + " objects added to database; consumed time: " + (time2 - time1));
            } else {
                LOGGER.error("Can't execute the command: properties missing");
                throw new CommandException("Can't execute the command: properties missing");
            }
        } catch (FileReadingException | FileWritingException | ParseException e) {
            LOGGER.error("Can't execute the command", e);
            throw new CommandException("Can't execute the command", e);
        }

    }

    /**
     * Creates query uris for current command.
     *
     * @param coordinatesPath path to file with coordinates
     * @param dates            download data on this dates
     * @return queue with uris for current command
     * @throws FileReadingException if method can't read file with coordinates
     */
    private Queue<String> createRequests(final String coordinatesPath, List<String> dates) throws FileReadingException {
        List<String[]> places = CsvReader.readLines(coordinatesPath);
        Queue<String> requestUris = new LinkedList<>();
        dates.forEach((date) -> {
            places.forEach((place) -> {
                String csvLng = place[1];
                String csvLat = place[2];
                List<NameValuePair> pairs = NameValuePairBuilder.newBuilder()
                        .addPair(Argument.DATE.name().toLowerCase(), date)
                        .addPair(Argument.LAT.name().toLowerCase(), csvLat)
                        .addPair(Argument.LNG.name().toLowerCase(), csvLng)
                        .build();
                requestUris.add(UriBuilder.buildUri(PoliceApi.ALL_CRIME, pairs));
            });
        });

        return requestUris;
    }


}
