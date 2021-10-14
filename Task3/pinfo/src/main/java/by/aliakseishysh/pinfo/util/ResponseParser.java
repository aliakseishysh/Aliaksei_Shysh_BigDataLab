package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.entity.AllCrimeResponseObject;

import static by.aliakseishysh.pinfo.database.DatabaseColumn.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResponseParser {

    private static final long index = 0;

    /**
     * Parses json string to {@code List<ResponseObject>}
     *
     * @param jsonString json string to parse
     * @return list with parsed json objects
     */
    public static List<AllCrimeResponseObject> parse(String jsonString) {
        List<AllCrimeResponseObject> resultList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        Iterator<Object> iterator = jsonArray.iterator();
        iterator.forEachRemaining((el) -> resultList.add(parseObject((JSONObject) el)));
        return resultList;
    }

    /**
     * Parses single json object and builds {@code ResponseObject}
     *
     * @param jsonObject json object
     * @return {@code ResponseObject} parsed from {@code JSONObject}
     */
    private static AllCrimeResponseObject parseObject(JSONObject jsonObject) {
        JSONObject location = jsonObject.getJSONObject(CRIMES_LOCATION);
        JSONObject street = location.getJSONObject(LOCATIONS_STREET);
        JSONObject outcomeStatus = jsonObject.optJSONObject(CRIMES_OUTCOME_STATUS);
        return AllCrimeResponseObject.newBuilder()
                .setCategory(jsonObject.getString(CRIMES_CATEGORY))
                .setPersistentId(jsonObject.getString(CRIMES_PERSISTENT_ID))
                .setMonth(jsonObject.getString(CRIMES_MONTH))
                .setLocationLatitude(location.getString(LOCATIONS_LATITUDE))
                .setLocationStreetId(street.getLong(STREETS_ID))
                .setLocationStreetName(street.getString(STREETS_NAME))
                .setLocationLongtitude(location.getString(LOCATIONS_LONGITUDE))
                .setContext(jsonObject.getString(CRIMES_CONTEXT))
                .setId(jsonObject.getLong(CRIMES_ID))
                .setLocationType(jsonObject.getString(CRIMES_LOCATION_TYPE))
                .setLocationSubtype(jsonObject.getString(CRIMES_LOCATION_SUBTYPE))
                .setOutcomeStatusCategory(outcomeStatus != null ? outcomeStatus.getString(OUTCOMES_CATEGORY) : null)
                .setOutcomeStatusDate(outcomeStatus != null ? outcomeStatus.getString(OUTCOMES_DATE) : null)
                .build();
    }


}
