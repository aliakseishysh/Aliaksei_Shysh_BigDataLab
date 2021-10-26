package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.command.sub.ForcesSubCommand;
import by.aliakseishysh.pinfo.dao.DatabaseColumn;
import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.dao.impl.StopAndSearchesDaoImpl;
import by.aliakseishysh.pinfo.exception.CommandException;
import by.aliakseishysh.pinfo.exception.FileException;
import by.aliakseishysh.pinfo.util.ArgumentValidator;
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
import java.util.stream.Collectors;

public class StopAndSearchesByForceCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopAndSearchesByForceCommand.class);
    private static final int LOG_AFTER_ADD_AMOUNT = 100;
    private static final int EMPTY_JSON_ARRAY_LENGTH = 2;

    /**
     * Downloading and processing data from stop and searches api
     *
     * @param properties command line arguments
     * @throws CommandException if command can't be performed
     */
    @Override
    public void execute(Properties properties) throws CommandException {
        try {
            String date = properties.getProperty(Argument.DATE.name().toLowerCase());
            String monthCountString = properties.getProperty(Argument.MONTH_COUNT.name().toLowerCase());
            if (!ArgumentValidator.validateDate(date) || !ArgumentValidator.validateMonthCount(monthCountString)) {
                LOGGER.error("Can't execute the command: property is not valid");
                throw new CommandException("Can't execute the command: property is not valid");
            } else {
                int monthCount = Integer.parseInt(monthCountString);
                List<String> dates = DateHelper.createDates(date, monthCount);
                List<Map<String, Object>> forces = ForcesSubCommand.downloadForces();
                Queue<String> requestUris = createRequests(dates, forces);

                List<String> responses = new DataDownloader().downloadAll(requestUris);
                responses = responses.stream().filter((str) -> str.length() != EMPTY_JSON_ARRAY_LENGTH).collect(Collectors.toList());

                Queue<Map<String, Object>> stopAndSearchesResponseObjects = new LinkedList<>();

                AtomicInteger counter = new AtomicInteger(0);
                AtomicInteger index = new AtomicInteger(0);

                long time1 = System.currentTimeMillis();
                PoliceApiDao policeDao = StopAndSearchesDaoImpl.getInstance();
                responses.forEach((rsp) -> stopAndSearchesResponseObjects.addAll(ResponseParser.parseStopAndSearchesByForceResponse(rsp)));
                while (!stopAndSearchesResponseObjects.isEmpty()) {
                    boolean result = policeDao.add(stopAndSearchesResponseObjects.poll());
                    if (result) {
                        int localIndex = index.get();
                        if (localIndex >= LOG_AFTER_ADD_AMOUNT) {
                            LOGGER.info(counter.get() + " objects added to database; objects remaining: " + stopAndSearchesResponseObjects.size());
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
     * @param dates  download data on this dates
     * @param forces download data for this forces
     * @return queue with uris for current command
     */
    private Queue<String> createRequests(List<String> dates, List<Map<String, Object>> forces) {
        Queue<String> requestUris = new LinkedList<>();
        dates.forEach((date) -> forces.forEach((force) -> {
            List<NameValuePair> pairs = NameValuePairBuilder.newBuilder()
                    .addPair(Argument.DATE.name().toLowerCase(), date)
                    .addPair(Argument.FORCE.name().toLowerCase(), (String) force.get(DatabaseColumn.FORCES_ID))
                    .build();
            requestUris.add((UriBuilder.buildUri(PoliceApi.STOP_AND_SEARCHES, pairs)));
        }));
        return requestUris;
    }

}
