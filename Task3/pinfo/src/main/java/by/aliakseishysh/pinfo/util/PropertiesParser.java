package by.aliakseishysh.pinfo.util;


import org.apache.commons.cli.*;

import java.util.Properties;

public class PropertiesParser {

    public static Properties parseOptions(String... args) {
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
            throw new UnsupportedOperationException(); // TODO Handle ParseException
        }
    }

    private static final Options buildOption(String optionName) {
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
