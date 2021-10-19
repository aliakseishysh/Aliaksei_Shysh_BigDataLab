package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.command.sub.ForcesSubCommand;
import by.aliakseishysh.pinfo.dao.DatabaseColumn;
import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.dao.impl.StopAndSearchesDaoImpl;
import by.aliakseishysh.pinfo.exception.CommandException;
import by.aliakseishysh.pinfo.exception.FileException;
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

public class StopAndSearchesByForceCommand implements Command{

    private static final Logger LOGGER = LoggerFactory.getLogger(StopAndSearchesByForceCommand.class);
    private static final String EMPTY_STRING = "";

    /**
     * Command to execute
     *
     * @param properties command line arguments
     * @throws CommandException if command can't be performed
     */
    @Override
    public void execute(Properties properties) throws CommandException {
        try {
            String date = properties.getProperty(Argument.DATE.name().toLowerCase());
            String monthCountString = properties.getProperty(Argument.MONTH_COUNT.name().toLowerCase());
            int monthCount = !monthCountString.equals("") ? Math.abs(Integer.parseInt(monthCountString)) : 1;
            if (!date.equals("")) {
                List<String> dates = DateHelper.createDates(date, monthCount);
                List<Map<String, Object>> forces = ForcesSubCommand.downloadForces();
                Queue<String> requestUris = createRequests(dates, forces);

                List<String> responses = new DataDownloader().downloadAll(requestUris);
                responses = responses.stream().filter((str) -> str.length() != 2).collect(Collectors.toList());

                Queue<Map<String, Object>> stopAndSearchesResponseObjects = new LinkedList<>();

                AtomicInteger counter = new AtomicInteger(1);
                AtomicInteger index = new AtomicInteger(1);

                long time1 = System.currentTimeMillis();
                PoliceApiDao policeDao = StopAndSearchesDaoImpl.getInstance();
                responses.forEach((rsp) -> stopAndSearchesResponseObjects.addAll(ResponseParser.parseStopAndSearchesByForceResponse(rsp)));
                stopAndSearchesResponseObjects.forEach((obj) -> {
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
        } catch (ParseException | NumberFormatException | FileException e) {
            LOGGER.error("Can't execute the command", e);
            throw new CommandException("Can't execute the command", e);
        }
    }

    /**
     * Creates query uris for current command.
     *
     * @param dates           download data on this dates
     * @param forces          download data for this forces
     * @return queue with uris for current command
     */
    private Queue<String> createRequests(List<String> dates, List<Map<String, Object>> forces) {
        Queue<String> requestUris = new LinkedList<>();
        dates.forEach((date) -> {
            forces.forEach((force) -> {
                List<NameValuePair> pairs = NameValuePairBuilder.newBuilder()
                        .addPair(Argument.DATE.name().toLowerCase(), date)
                        .addPair(Argument.FORCE.name().toLowerCase(), (String) force.get(DatabaseColumn.FORCES_ID))
                        .build();
                requestUris.add((UriBuilder.buildUri(PoliceApi.STOP_AND_SEARCHES, pairs)));
            });
        });
        return requestUris;
    }

}
