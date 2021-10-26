package by.aliakseishysh.pinfo.command.sub;

import by.aliakseishysh.pinfo.command.PoliceApi;
import by.aliakseishysh.pinfo.util.DataDownloader;
import by.aliakseishysh.pinfo.util.ResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Subcommand to download list of forces
 */
public class ForcesSubCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForcesSubCommand.class);

    public static List<Map<String, Object>> downloadForces() {
        List<Map<String, Object>> result = null;
        try {
            List<String> responses = new DataDownloader().downloadAll(new LinkedList<>(Collections.singletonList(PoliceApi.FORCES)));
            String forces = responses.stream().findFirst().get();
            result = ResponseParser.parseForcesResponse(forces);
        } catch (NoSuchElementException e) {
            LOGGER.error("Can't get forces list", e);
        }
        return result;

    }
}
