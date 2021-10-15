package by.aliakseishysh.pinfo.util;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DataDownloaderTest {

    private static final String correctUri = "https://data.police.uk/api/crimes-street/all-crime?lat=52.629729&lng=-1.131592&date=2018-9";
    private static final String noDataUri = "https://data.police.uk/api/crimes-street/all-crime?lat=52.629729&lng=-1.131592&date=1997-6";
    private DataDownloader dataDownloader;
    private Queue<String> uris;

    @BeforeMethod
    public void setup() {
        dataDownloader = new DataDownloader();
        uris = new LinkedList<>();
    }

    @Test
    public void  downloadAllOneUriTest() {
        uris.add(correctUri);
        List<String> responses = dataDownloader.downloadAll(uris);
        Assert.assertEquals(responses.size(), 1);
    }

    @Test
    public void  downloadAllOneNoDataUriTest() {
        uris.add(noDataUri);
        List<String> responses = dataDownloader.downloadAll(uris);
        Assert.assertEquals(responses.size(), 0);
    }

}
