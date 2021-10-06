package by.aliakseishysh.pinfo.controller;

import by.aliakseishysh.pinfo.controller.command.Argument;
import by.aliakseishysh.pinfo.controller.command.Command;
import by.aliakseishysh.pinfo.controller.command.CommandDefiner;

import java.util.Properties;

public class Controller {

    public static void processRequest(String... arguments) {
        Properties properties = PropertiesParser.parseOptions(arguments);
        Command command = CommandDefiner.defineCommand(properties.getProperty(Argument.COMMAND.name().toLowerCase()));
        command.execute(properties);

    }

}
