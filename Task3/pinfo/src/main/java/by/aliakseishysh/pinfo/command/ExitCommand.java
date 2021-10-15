package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.exception.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ExitCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExitCommand.class);
    /**
     * Command to execute
     *
     * @param properties command line arguments
     * @throws CommandException if command can't be performed
     */
    @Override
    public void execute(Properties properties) throws CommandException {
        LOGGER.error("Can't find required api name: " + properties.getProperty(Argument.COMMAND.name().toLowerCase()));
        throw new CommandException("Can't find required api name: "
                + properties.getProperty(Argument.COMMAND.name().toLowerCase()));
    }
}
