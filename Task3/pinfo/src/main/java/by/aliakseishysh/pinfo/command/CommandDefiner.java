package by.aliakseishysh.pinfo.command;

public enum CommandDefiner {

    ALL_CRIME("all-crime", new AllCrimeCommand()),
    EXIT("exit", null); // TODO create command

    private final String name;
    private final Command command;

    public String getName() {
        return name;
    }

    public Command getCommand() {
        return command;
    }

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
}
