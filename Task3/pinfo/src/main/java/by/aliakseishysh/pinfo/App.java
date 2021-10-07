package by.aliakseishysh.pinfo;

import by.aliakseishysh.pinfo.command.PoliceApi;
import by.aliakseishysh.pinfo.util.CsvReader;
import by.aliakseishysh.pinfo.util.DataDownloader;
import by.aliakseishysh.pinfo.util.PropertiesParser;
import by.aliakseishysh.pinfo.command.Argument;
import by.aliakseishysh.pinfo.command.Command;
import by.aliakseishysh.pinfo.command.CommandDefiner;
import io.github.bucket4j.*;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Main class
 */
public class App {

    public static void main(String... args) throws InterruptedException {
        String[] test = new String[3];
        test[0] = "-Dcommand=all-crime";
        test[1] = "-Dfile_path=C:\\Data\\Java\\Workspace\\epam_lab\\Task3\\LondonStations.csv";
        test[2] = "-Ddate=2021-01";

        // TODO part 1
        Properties properties = PropertiesParser.parseOptions(test);
        Command command = CommandDefiner.defineCommand(properties.getProperty(Argument.COMMAND.name().toLowerCase()));
        command.execute(properties);
        //---------------------------------------
    }




}
