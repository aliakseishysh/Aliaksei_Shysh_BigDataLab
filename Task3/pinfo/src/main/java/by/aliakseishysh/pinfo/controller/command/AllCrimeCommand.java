package by.aliakseishysh.pinfo.controller.command;

import by.aliakseishysh.pinfo.model.util.CsvReader;
import by.aliakseishysh.pinfo.model.util.Url;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AllCrimeCommand implements Command {
    public void execute(Properties properties) {
        String coordinatesPath = properties.getProperty(Argument.FILE_PATH.name().toLowerCase());
        String date = properties.getProperty(Argument.DATE.name().toLowerCase());
        if (coordinatesPath != null && date != null) {
            // TODO proceed request further
            String dateName = Argument.DATE.name().toLowerCase();
            String latName = Argument.LAT.name().toLowerCase();
            String lngName = Argument.LNG.name().toLowerCase();
            Map<String, String> argumentMap = new HashMap<>(4);

            List<String[]> places = CsvReader.readLines(coordinatesPath);

            for (String[] line: places) {
                String csvName = line[0];
                String csvLng = line[1];
                String csvLat = line[2];

                argumentMap.put(dateName, date);
                argumentMap.put(latName, csvLat);
                argumentMap.put(lngName, csvLng);

                String urlWithArguments = Url.appendArguments(PoliceApi.ALL_CRIME.getApi(), argumentMap);
                // TODO download data, parse it and push to database
                // 1) make request by urlWithArguments
                // 2) parse it into List<String>
                // 3) push to database


                // ----------

                argumentMap.clear();

            }



        } else {
            // TODO handle parameter input error
        }
    }
}
