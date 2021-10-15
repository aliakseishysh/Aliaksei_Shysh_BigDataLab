package by.aliakseishysh.pinfo.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UriBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(UriBuilder.class);

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
            LOGGER.error("URISyntaxException occurred: " + baseUri + " " +
                    parameters
                            .stream()
                            .flatMap((nvm) -> Stream.of(nvm.getName() + ": " + nvm.getValue()))
                            .collect(Collectors.joining("; "))
            );
            return baseUri;
        }
    }

}
