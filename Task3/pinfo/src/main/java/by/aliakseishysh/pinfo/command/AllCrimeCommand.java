package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.dao.impl.PoliceApiDaoFileImpl;
import by.aliakseishysh.pinfo.dao.impl.PoliceApiDaoImpl;
import by.aliakseishysh.pinfo.exception.CommandException;
import by.aliakseishysh.pinfo.exception.ReadingException;
import by.aliakseishysh.pinfo.util.CsvReader;
import by.aliakseishysh.pinfo.util.DataDownloader;
import by.aliakseishysh.pinfo.util.NameValuePairBuilder;
import by.aliakseishysh.pinfo.util.ResponseParser;
import by.aliakseishysh.pinfo.util.UriBuilder;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            String saveToFile = properties.getProperty(Argument.SAVE_TO_FILE.name().toLowerCase());
            if (coordinatesPath != null && date != null) {
                List<String> responses = new DataDownloader().downloadAll(createRequests(coordinatesPath, date));
                ConcurrentLinkedQueue<Map<String, Object>> allCrimeResponseObjects = new ConcurrentLinkedQueue<>();
                PoliceApiDao policeDao;
                AtomicInteger counter = new AtomicInteger(1);
                AtomicInteger index = new AtomicInteger(1);
                // TODO rewrite this to achieve abstraction
                long time1 = System.currentTimeMillis();
                if (saveToFile != null)  {
                    policeDao = PoliceApiDaoFileImpl.getInstance();
                    policeDao.init(saveToFile);
                } else {
                    policeDao = PoliceApiDaoImpl.getInstance();
                    policeDao.init();
                }
                responses.forEach((rsp) -> allCrimeResponseObjects.addAll(ResponseParser.parse(rsp)));
                allCrimeResponseObjects.forEach((obj) -> {
                    policeDao.addNewAllCrimeResponseObject(obj);
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
        } catch (ReadingException e) {
            LOGGER.error("Can't execute the command", e);
            throw new CommandException("Can't execute the command", e);
        }

    }

    /**
     * Creates query uris for current command.
     *
     * @param coordinatesPath path to file with coordinates
     * @param date            download data on this date
     * @return queue with uris for current command
     * @throws ReadingException if method can't read file with coordinates
     */
    private Queue<String> createRequests(final String coordinatesPath, final String date) throws ReadingException {
        List<String[]> places = CsvReader.readLines(coordinatesPath);
        Queue<String> requestUris = new LinkedList<>();
        places.forEach((place) -> {
            String csvLng = place[1];
            String csvLat = place[2];
            List<NameValuePair> pairs = NameValuePairBuilder.newBuilder()
                    .addPair(Argument.DATE.name().toLowerCase(), date)
                    .addPair(Argument.LAT.name().toLowerCase(), csvLat)
                    .addPair(Argument.LNG.name().toLowerCase(), csvLng)
                    .build();
            String uri = UriBuilder.buildUri(PoliceApi.ALL_CRIME, pairs);
            requestUris.add(uri);
        });
        return requestUris;
    }


}
