package by.aliakseishysh.pinfo.command;

/**
 * Class for defining commands
 */
public enum CommandDefiner {

    ALL_CRIME("all-crime", new AllCrimeCommand()),
    EXIT("exit", new ExitCommand());

    private final String name;
    private final Command command;

    CommandDefiner(String name, Command command) {
        this.name = name;
        this.command = command;
    }

    public static Command defineCommand(String command) {
        CommandDefiner commandDefiner = CommandDefiner.fromString(command);
        return commandDefiner.getCommand();
    }

    private static CommandDefiner fromString(String commandName) {
        CommandDefiner result = EXIT;
        for (CommandDefiner commandDefiner : CommandDefiner.values()) {
            if (commandDefiner.getName().equals(commandName)) {
                result = commandDefiner;
                break;
            }
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public Command getCommand() {
        return command;
    }
}
