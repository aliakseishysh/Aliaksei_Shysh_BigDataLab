package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.PinfoParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Properties;

public class PropertiesParser {

    /**
     * Parses command line arguments
     *
     * @param args command line arguments
     * @return properties with command line arguments
     */
    public static Properties parseOptions(String... args) throws PinfoParseException {
        try {
            final CommandLineParser parser = new DefaultParser();
            final String optionName = "D";
            final CommandLine commandLine = parser.parse(buildOptions(optionName), args);
            Properties properties = new Properties();
            if (commandLine.hasOption(optionName)) {
                properties = commandLine.getOptionProperties(optionName);
            }
            return properties;
        } catch (org.apache.commons.cli.ParseException e) {
            throw new PinfoParseException("Can't parse command line arguments", e);
        }
    }

    /**
     * Builds options list
     *
     * @param optionName command line option name (e.g. D)
     * @return list with options settings
     */
    private static Options buildOptions(String optionName) {
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
