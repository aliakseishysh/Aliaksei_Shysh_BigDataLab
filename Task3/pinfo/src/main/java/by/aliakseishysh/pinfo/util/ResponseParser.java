package by.aliakseishysh.pinfo.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static by.aliakseishysh.pinfo.dao.DatabaseColumn.*;


public class ResponseParser {

    private static final long index = 0;

    /**
     * Parses json string to {@code List<ResponseObject>}
     *
     * @param jsonString json string to parse
     * @return list with parsed json objects
     */
    public static List<Map<String, Object>> parse(String jsonString) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        Iterator<Object> iterator = jsonArray.iterator();
        iterator.forEachRemaining((el) -> resultList.add(parseAllCrimeResponse((JSONObject) el)));
        return resultList;
    }

    /**
     * Parses single json object and builds {@code ResponseObject}
     *
     * @param jsonObject json object
     * @return {@code ResponseObject} parsed from {@code JSONObject}
     */
    private static Map<String, Object> parseAllCrimeResponse(JSONObject jsonObject) {
        JSONObject location = jsonObject.getJSONObject(CRIMES_LOCATION);
        JSONObject street = location.getJSONObject(LOCATIONS_STREET);
        JSONObject outcome = jsonObject.optJSONObject(CRIMES_OUTCOME_STATUS);
        Map<String, Object> crimes = new HashMap<>();
        Map<String, Object> streets = new HashMap<>();
        Map<String, Object> locations = new HashMap<>();
        Map<String, Object> outcomes = outcome != null ? new HashMap<>() : null;

        crimes.put(CRIMES_CATEGORY, jsonObject.getString(CRIMES_CATEGORY));
        crimes.put(CRIMES_PERSISTENT_ID, jsonObject.getString(CRIMES_PERSISTENT_ID));
        crimes.put(CRIMES_MONTH, Date.valueOf(jsonObject.getString(CRIMES_MONTH)+"-01"));
        locations.put(LOCATIONS_LATITUDE, location.getString(LOCATIONS_LATITUDE));
        streets.put(STREETS_ID, street.getLong(STREETS_ID));
        streets.put(STREETS_NAME, street.getString(STREETS_NAME));
        locations.put(LOCATIONS_LONGITUDE, location.getString(LOCATIONS_LONGITUDE));
        crimes.put(CRIMES_CONTEXT, jsonObject.getString(CRIMES_CONTEXT));
        crimes.put(CRIMES_ID, jsonObject.getLong(CRIMES_ID));
        crimes.put(CRIMES_LOCATION_TYPE, jsonObject.getString(CRIMES_LOCATION_TYPE));
        crimes.put(CRIMES_LOCATION_SUBTYPE, jsonObject.getString(CRIMES_LOCATION_SUBTYPE));
        if (outcomes != null) {
            outcomes.put(OUTCOMES_CATEGORY, outcome.getString(OUTCOMES_CATEGORY));
            outcomes.put(OUTCOMES_DATE, Date.valueOf(outcome.getString(OUTCOMES_DATE)+"-01"));
        }
        crimes.put(LOCATIONS, locations);
        locations.put(STREETS, streets);
        crimes.put(OUTCOMES, outcomes);
        return crimes;
        //return AllCrimeResponseObject.newBuilder()
                //.setCategory(jsonObject.getString(CRIMES_CATEGORY))
                //.setPersistentId(jsonObject.getString(CRIMES_PERSISTENT_ID))
                //.setMonth(jsonObject.getString(CRIMES_MONTH))
                //.setLocationLatitude(location.getString(LOCATIONS_LATITUDE))
                //.setLocationStreetId(street.getLong(STREETS_ID))
                //.setLocationStreetName(street.getString(STREETS_NAME))
                //.setLocationLongtitude(location.getString(LOCATIONS_LONGITUDE))
                //.setContext(jsonObject.getString(CRIMES_CONTEXT))
                //.setId(jsonObject.getLong(CRIMES_ID))
                //.setLocationType(jsonObject.getString(CRIMES_LOCATION_TYPE))
                //.setLocationSubtype(jsonObject.getString(CRIMES_LOCATION_SUBTYPE))
                //.setOutcomeStatusCategory(outcome != null ? outcome.getString(OUTCOMES_CATEGORY) : null)
                //.setOutcomeStatusDate(outcome != null ? outcome.getString(OUTCOMES_DATE) : null)
                //.build();
    }


}
