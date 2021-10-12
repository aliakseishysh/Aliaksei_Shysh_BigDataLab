package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.database.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.database.dao.impl.PoliceApiDaoImpl;
import by.aliakseishysh.pinfo.entity.ResponseObject;
import by.aliakseishysh.pinfo.util.CsvReader;
import by.aliakseishysh.pinfo.util.DataDownloader;
import by.aliakseishysh.pinfo.util.ResponseParser;
import org.apache.http.client.utils.URIBuilder;

import javax.xml.ws.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class AllCrimeCommand implements Command {
    public void execute(Properties properties) {
        String coordinatesPath = properties.getProperty(Argument.FILE_PATH.name().toLowerCase());
        String date = properties.getProperty(Argument.DATE.name().toLowerCase());
        if (coordinatesPath != null && date != null) {
            List<String> responses = new DataDownloader().downloadAll(createRequests(coordinatesPath, date));
            // TODO implement
            List<ResponseObject> responseObjects = new ResponseParser().parse(responses.get(0));

            PoliceApiDao policeDao = PoliceApiDaoImpl.getInstance();
            System.out.println("RESPONSE OBJECT " + responseObjects.get(0));
            policeDao.addNewResponseObject(responseObjects.get(0));




        } else {
            // TODO handle parameter input error
        }
    }

    private Queue<String> createRequests(String coordinatesPath, String date) {
        String dateName = Argument.DATE.name().toLowerCase();
        String latName = Argument.LAT.name().toLowerCase();
        String lngName = Argument.LNG.name().toLowerCase();
        List<String[]> places = CsvReader.readLines(coordinatesPath);
        Queue<String> requestUris = new LinkedList<>();
        for (String[] line : places) {
            String csvName = line[0];
            String csvLng = line[1];
            String csvLat = line[2];

            try {
                URI uri = new URIBuilder(PoliceApi.ALL_CRIME.getApi())
                        .addParameter(dateName, date)
                        .addParameter(latName, csvLat)
                        .addParameter(lngName, csvLng)
                        .build();
                requestUris.add(uri.toString());
            } catch (URISyntaxException e) {
                throw new UnsupportedOperationException(); // TODO handle exception
            }
        }
        return requestUris;
    }

}
