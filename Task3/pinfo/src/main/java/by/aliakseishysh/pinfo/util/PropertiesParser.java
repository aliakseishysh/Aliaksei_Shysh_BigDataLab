package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.CliPropertiesException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class PropertiesParser {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesParser.class);

    /**
     * Parses command line arguments
     *
     * @param args command line arguments
     * @return properties with command line arguments
     */
    public static Properties parseOptions(String... args) throws CliPropertiesException {
        try {
            final CommandLineParser parser = new DefaultParser();
            final String optionName = "D";
            final CommandLine commandLine = parser.parse(buildOption(optionName), args);
            Properties properties = new Properties();
            if (commandLine.hasOption(optionName)) {
                properties = commandLine.getOptionProperties(optionName);
            }
            return properties;
        } catch (ParseException e) {
            logger.error("Can't parse command line arguments", e);
            throw new CliPropertiesException("Can't parse command line arguments", e);
        }
    }

    /**
     * Builds options list
     *
     * @param optionName command line option name (e.g. D)
     * @return list with options settings
     */
    private static Options buildOption(String optionName) {
        final Options options = new Options();
        final Option propertyOption = Option.builder()
                .longOpt(optionName)
                .argName("property=value")
                .hasArg()
                .valueSeparator()
                .numberOfArgs(2)
                .desc("use value for given properties")
                .build();
        options.addOption(propertyOption);
        return options;
    }

}
