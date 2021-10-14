package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.database.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.database.dao.impl.PoliceApiDaoImpl;
import by.aliakseishysh.pinfo.entity.AllCrimeResponseObject;
import by.aliakseishysh.pinfo.exception.CommandException;
import by.aliakseishysh.pinfo.exception.ReadingException;
import by.aliakseishysh.pinfo.util.*;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Command for downloading data from all-crime api
 */
public class AllCrimeCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllCrimeCommand.class);

    // TODO add option "save to file"

    /**
     * Downloading and processing data from api
     *
     * @param properties command line arguments
     * @throws CommandException if method can't execute the command
     */
    public void execute(Properties properties) throws CommandException {
        try {
            String coordinatesPath = properties.getProperty(Argument.FILE_PATH.name().toLowerCase());
            String date = properties.getProperty(Argument.DATE.name().toLowerCase());
            if (coordinatesPath != null && date != null) {
                List<String> responses = new DataDownloader().downloadAll(createRequests(coordinatesPath, date));
                List<AllCrimeResponseObject> allCrimeResponseObjects = new LinkedList<>();
                responses.forEach((rsp) -> allCrimeResponseObjects.addAll(ResponseParser.parse(rsp)));
                PoliceApiDao policeDao = PoliceApiDaoImpl.getInstance();
                allCrimeResponseObjects.forEach(policeDao::addNewAllCrimeResponseObject);
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
     * Creates query uris for current command
     *
     * @param coordinatesPath path to file with coordinates
     * @param date            download data on this date
     * @return queue with uris for current command
     * @throws ReadingException if method can't read file with coordinates
     */
    private Queue<String> createRequests(String coordinatesPath, String date) throws ReadingException {
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
