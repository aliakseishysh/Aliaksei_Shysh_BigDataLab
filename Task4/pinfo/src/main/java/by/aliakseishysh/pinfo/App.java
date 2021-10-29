package by.aliakseishysh.pinfo;

import by.aliakseishysh.pinfo.command.Argument;
import by.aliakseishysh.pinfo.command.Command;
import by.aliakseishysh.pinfo.command.CommandDefiner;
import by.aliakseishysh.pinfo.dao.FluentConnector;
import by.aliakseishysh.pinfo.dao.HcpSource;
import by.aliakseishysh.pinfo.exception.CommandException;
import by.aliakseishysh.pinfo.exception.PinfoParseException;
import by.aliakseishysh.pinfo.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


/**
 * Main class for initialization and running commands
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * Main method
     *
     * @param args command line arguments
     */
    public static void main(String... args) throws PinfoParseException, CommandException {
        logger.info("Initialization...");
        init();
        logger.info("Command line arguments parsing...");
        Properties properties = PropertiesParser.parseOptions(args);
        logger.info("Command defining...");
        Command command = CommandDefiner.defineCommand(properties.getProperty(Argument.COMMAND.name().toLowerCase()));
        logger.info("Command execution...");
        command.execute(properties);
    }

    /**
     * Initializes HikariCP and FluentJDBC
     */
    private static void init() {
        // HcpSource.getSource();
        // FluentConnector.getConnector();
    }


}
