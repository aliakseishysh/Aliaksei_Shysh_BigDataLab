package by.aliakseishysh.pinfo.model.util;

import java.util.HashMap;
import java.util.Map;

public class Url {

    public static String appendArguments(String url, Map<String, String> arguments) {
        int index = 0;
        int mapSize = arguments.size();
        if (mapSize == 0) {
            return url;
        }
        StringBuffer newUrl = new StringBuffer(url);
        for (Map.Entry entry : arguments.entrySet()) {
            if (index == 0) {
                newUrl.append("?");
            }
            newUrl.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
            if (index + 1 < mapSize) {
                newUrl.append("&");
            }
            index += 1;
        }
        return newUrl.toString();
    }

}
