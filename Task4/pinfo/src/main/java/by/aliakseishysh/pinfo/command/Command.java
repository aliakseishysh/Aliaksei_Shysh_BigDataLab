package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.exception.CommandException;

import java.util.Properties;

public interface Command {

    /**
     * Command to execute
     *
     * @param properties command line arguments
     * @throws CommandException if command can't be performed
     */
    void execute(Properties properties) throws CommandException;
}
