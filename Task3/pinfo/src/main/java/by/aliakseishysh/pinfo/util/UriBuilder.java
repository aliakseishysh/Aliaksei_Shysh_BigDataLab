package by.aliakseishysh.pinfo.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class UriBuilder {

    /**
     * Method for building uris.
     *
     * @param baseUri    base uri
     * @param parameters uri parameters to add
     * @return result uri with added parameters
     */
    public static String buildUri(String baseUri, List<NameValuePair> parameters) {
        try {
            URI uri = new URIBuilder(baseUri)
                    .addParameters(parameters)
                    .build();
            return uri.toString();
        } catch (URISyntaxException e) {
            throw new UnsupportedOperationException(); // TODO handle exception
        }
    }

}
