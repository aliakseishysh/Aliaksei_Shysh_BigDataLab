package by.aliakseishysh.pinfo;

import by.aliakseishysh.pinfo.database.FluentConnector;
import by.aliakseishysh.pinfo.database.HcpSource;
import by.aliakseishysh.pinfo.util.PropertiesParser;
import by.aliakseishysh.pinfo.command.Argument;
import by.aliakseishysh.pinfo.command.Command;
import by.aliakseishysh.pinfo.command.CommandDefiner;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Query;

import java.util.*;
import java.util.stream.Stream;

/**
 * Main class
 */
public class App {

    public static void main(String... args) throws InterruptedException {
        // TODO part 1
        init();
        Properties properties = PropertiesParser.parseOptions(args);
        Command command = CommandDefiner.defineCommand(properties.getProperty(Argument.COMMAND.name().toLowerCase()));
        command.execute(properties);

        //---------------------------------------

    }

    private static void init() {
        HcpSource.getSource();
        FluentConnector.getConnector();
    }




}
