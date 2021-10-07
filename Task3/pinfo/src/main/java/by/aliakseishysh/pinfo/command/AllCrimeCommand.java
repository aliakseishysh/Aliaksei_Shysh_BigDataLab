package by.aliakseishysh.pinfo.command;

import by.aliakseishysh.pinfo.util.CsvReader;
import by.aliakseishysh.pinfo.util.DataDownloader;
import by.aliakseishysh.pinfo.util.Url;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class AllCrimeCommand implements Command {
    public void execute(Properties properties) {
        List<String> responses = Collections.synchronizedList(new ArrayList<>());
        String coordinatesPath = properties.getProperty(Argument.FILE_PATH.name().toLowerCase());
        String date = properties.getProperty(Argument.DATE.name().toLowerCase());
        if (coordinatesPath != null && date != null) {
            try {
                DataDownloader.downloadAll(createRequests(coordinatesPath, date));
            } catch (InterruptedException e) {
                e.printStackTrace(); // TODO handle exception
            }
            responses.forEach((x) -> System.out.println(x.substring(1,100)));


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
