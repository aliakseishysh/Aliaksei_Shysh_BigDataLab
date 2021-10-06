package by.aliakseishysh.pinfo;

import by.aliakseishysh.pinfo.controller.Controller;
import by.aliakseishysh.pinfo.controller.PropertiesParser;
import by.aliakseishysh.pinfo.controller.command.Argument;
import by.aliakseishysh.pinfo.controller.command.Command;
import by.aliakseishysh.pinfo.controller.command.CommandDefiner;

import java.util.Properties;

/**
 * Main class
 */
public class App {



    public static void main(String... args) {
        String[] test = new String[3];
        test[0] = "-Dcommand=test1";
        test[1] = "-Dplace=test2";
        test[2] = "-Ddate=test3";

        Controller.processRequest(args);
        // 1) узнать какая команда была передана
        // 2) узнать какие аргументы были переданы
        // 3) передать в команду аргументы и выполнить её
    }
}
