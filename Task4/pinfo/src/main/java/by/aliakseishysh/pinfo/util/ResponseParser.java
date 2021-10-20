package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.exception.PinfoParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static by.aliakseishysh.pinfo.dao.DatabaseColumn.*;


public class ResponseParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseParser.class);

    /**
     * Parses json string to {@code List<Map<String, Object>>}
     *
     * @param jsonString json string to parse
     * @return list with parsed json objects
     */
    public static List<Map<String, Object>> parseAllCrimeResponse(String jsonString) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        Iterator<Object> iterator = jsonArray.iterator();
        iterator.forEachRemaining((el) -> resultList.add(parseAllCrimeResponseInternal((JSONObject) el)));
        return resultList;
    }

    /**
     * Parses json string to {@code List<Map<String, Object>>}
     *
     * @param jsonString json string to parse
     * @return list with parsed json objects
     */
    public static List<Map<String, Object>> parseStopAndSearchesByForceResponse(String jsonString) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        Iterator<Object> iterator = jsonArray.iterator();
        iterator.forEachRemaining((el) -> {
            try {
                resultList.add(parseStopAndSearchesByForceResponseInternal((JSONObject) el));
            } catch (PinfoParseException e) {
                LOGGER.error("Can't parse date", e);
            }
        });
        return resultList;
    }

    /**
     * Parses json string to {@code List<Map<String, Object>>}
     *
     * @param jsonString json string to parse
     * @return list with parsed json objects
     */
    public static List<Map<String, Object>> parseForcesResponse(String jsonString) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        Iterator<Object> iterator = jsonArray.iterator();
        iterator.forEachRemaining((el) -> resultList.add(parseForcesResponseInternal((JSONObject) el)));
        return resultList;
    }

    /**
     * Parses single json object and builds {@code List<Map<String, Object>>}
     *
     * @param jsonObject json object
     * @return {@code ResponseObject} parsed from {@code JSONObject}
     */
    private static Map<String, Object> parseAllCrimeResponseInternal(JSONObject jsonObject) {
        JSONObject location = jsonObject.getJSONObject(SAS_LOCATION);
        JSONObject street = location.getJSONObject(LOCATIONS_STREET);
        JSONObject outcome = jsonObject.optJSONObject(CRIMES_OUTCOME_STATUS);
        Map<String, Object> crimes = new HashMap<>();
        Map<String, Object> streets = new HashMap<>();
        Map<String, Object> locations = new HashMap<>();
        Map<String, Object> outcomes = outcome != null ? new HashMap<>() : null;

        crimes.put(CRIMES_CATEGORY, jsonObject.getString(CRIMES_CATEGORY) == JSONObject.NULL ? null : jsonObject.getString(CRIMES_CATEGORY));
        crimes.put(CRIMES_PERSISTENT_ID, jsonObject.getString(CRIMES_PERSISTENT_ID) == JSONObject.NULL ? null : jsonObject.getString(CRIMES_PERSISTENT_ID));
        crimes.put(CRIMES_MONTH, Date.valueOf(jsonObject.getString(CRIMES_MONTH) + "-01"));
        locations.put(LOCATIONS_LATITUDE, location.getString(LOCATIONS_LATITUDE) == JSONObject.NULL ? null : location.getString(LOCATIONS_LATITUDE));
        streets.put(STREETS_ID, street.getLong(STREETS_ID));
        streets.put(STREETS_NAME, street.getString(STREETS_NAME) == JSONObject.NULL ? null : street.getString(STREETS_NAME));
        locations.put(LOCATIONS_LONGITUDE, location.getString(LOCATIONS_LONGITUDE) == JSONObject.NULL ? null : location.getString(LOCATIONS_LONGITUDE));
        crimes.put(CRIMES_CONTEXT, jsonObject.getString(CRIMES_CONTEXT) == JSONObject.NULL ? null : jsonObject.getString(CRIMES_CONTEXT));
        crimes.put(CRIMES_ID, jsonObject.getLong(CRIMES_ID));
        crimes.put(CRIMES_LOCATION_TYPE, jsonObject.getString(CRIMES_LOCATION_TYPE) == JSONObject.NULL ? null : jsonObject.getString(CRIMES_LOCATION_TYPE));
        crimes.put(CRIMES_LOCATION_SUBTYPE, jsonObject.getString(CRIMES_LOCATION_SUBTYPE) == JSONObject.NULL ? null : jsonObject.getString(CRIMES_LOCATION_SUBTYPE));
        if (outcomes != null) {
            outcomes.put(OUTCOMES_CATEGORY, outcome.getString(OUTCOMES_CATEGORY) == JSONObject.NULL ? null : outcome.getString(OUTCOMES_CATEGORY));
            outcomes.put(OUTCOMES_DATE, Date.valueOf(outcome.getString(OUTCOMES_DATE) + "-01"));
        }
        crimes.put(LOCATIONS, locations);
        locations.put(STREETS, streets);
        crimes.put(OUTCOMES, outcomes);
        return crimes;
    }

    /**
     * Parses single json object and builds {@code List<Map<String, Object>>}
     *
     * @param jsonObject json object
     * @return {@code ResponseObject} parsed from {@code JSONObject}
     */
    private static Map<String, Object> parseStopAndSearchesByForceResponseInternal(JSONObject jsonObject) throws PinfoParseException {
        JSONObject location = jsonObject.optJSONObject(SAS_LOCATION);
        JSONObject street = null;
        if (location != null) {
            street = location.optJSONObject(LOCATIONS_STREET);
        }

        JSONObject outcomeObject = jsonObject.optJSONObject(SAS_OUTCOME_OBJECT);
        Map<String, Object> sas = new HashMap<>();
        Map<String, Object> streets = street == null || street == JSONObject.NULL ? null : new HashMap<>();
        Map<String, Object> locations = location == null || location == JSONObject.NULL ? null : new HashMap<>();
        Map<String, Object> outcomeObjects = outcomeObject == null || outcomeObject == JSONObject.NULL ? null : new HashMap<>();

        sas.put(SAS_TYPE, jsonObject.get(SAS_TYPE));
        sas.put(SAS_INVOLVED_PERSON, jsonObject.get(SAS_INVOLVED_PERSON));
        sas.put(SAS_DATETIME, DateHelper.parseDate(jsonObject.getString(SAS_DATETIME)));

        sas.put(SAS_OPERATION, jsonObject.get(SAS_OPERATION) == JSONObject.NULL ? null : jsonObject.get(SAS_OPERATION));
        sas.put(SAS_OPERATION_NAME, jsonObject.get(SAS_OPERATION_NAME) == JSONObject.NULL ? null : jsonObject.get(SAS_OPERATION_NAME));

        if (location != null && location != JSONObject.NULL) {
            locations.put(LOCATIONS_LATITUDE, location.getString(LOCATIONS_LATITUDE) == JSONObject.NULL ? null : location.getString(LOCATIONS_LATITUDE));
            if (street != null && street != JSONObject.NULL) {
                streets.put(STREETS_ID, street.getLong(STREETS_ID));
                streets.put(STREETS_NAME, street.getString(STREETS_NAME));
                locations.put(STREETS, streets);
            }
            locations.put(LOCATIONS_LONGITUDE, location.getString(LOCATIONS_LONGITUDE));
        }


        sas.put(SAS_GENDER, jsonObject.get(SAS_GENDER) == JSONObject.NULL ? null : jsonObject.get(SAS_GENDER));
        sas.put(SAS_AGE_RANGE, jsonObject.get(SAS_AGE_RANGE) == JSONObject.NULL ? null : jsonObject.get(SAS_AGE_RANGE));
        sas.put(SAS_SELF_DEFINED_ETHNICITY, jsonObject.get(SAS_SELF_DEFINED_ETHNICITY) == JSONObject.NULL ? null : jsonObject.get(SAS_SELF_DEFINED_ETHNICITY));
        sas.put(SAS_OFFICER_DEFINED_ETHNICITY, jsonObject.get(SAS_OFFICER_DEFINED_ETHNICITY) == JSONObject.NULL ? null : jsonObject.get(SAS_OFFICER_DEFINED_ETHNICITY));
        sas.put(SAS_LEGISLATION, jsonObject.get(SAS_LEGISLATION) == JSONObject.NULL ? null : jsonObject.get(SAS_LEGISLATION));
        sas.put(SAS_OBJECT_OF_SEARCH, jsonObject.get(SAS_OBJECT_OF_SEARCH) == JSONObject.NULL ? null : jsonObject.get(SAS_OBJECT_OF_SEARCH));

        // TODO not in documentation, but in response
        if (outcomeObject != null && outcomeObject != JSONObject.NULL) {
            outcomeObjects.put(OUTCOME_OBJECTS_ID, outcomeObject.get(OUTCOME_OBJECTS_ID) == JSONObject.NULL ? null : outcomeObject.get(OUTCOME_OBJECTS_ID));
            outcomeObjects.put(OUTCOME_OBJECTS_NAME, outcomeObject.get(OUTCOME_OBJECTS_NAME) == JSONObject.NULL ? null : outcomeObject.get(OUTCOME_OBJECTS_NAME));
        }
        sas.put(SAS_OUTCOME, jsonObject.get(SAS_OUTCOME) == JSONObject.NULL ? null : jsonObject.get(SAS_OUTCOME));
        sas.put(SAS_OUTCOME_LINKED_TO_OBJECT_OF_SEARCH, jsonObject.get(SAS_OUTCOME_LINKED_TO_OBJECT_OF_SEARCH) == JSONObject.NULL ? null : jsonObject.get(SAS_OUTCOME_LINKED_TO_OBJECT_OF_SEARCH));
        sas.put(SAS_REMOVAL_OF_MORE_THAN_OUTER_CLOTHING, jsonObject.get(SAS_REMOVAL_OF_MORE_THAN_OUTER_CLOTHING) == JSONObject.NULL ? null : jsonObject.get(SAS_REMOVAL_OF_MORE_THAN_OUTER_CLOTHING));

        sas.put(LOCATIONS, locations);
        sas.put(OUTCOME_OBJECTS, outcomeObjects);
        return sas;


    }

    /**
     * Parses single json object and builds {@code List<Map<String, Object>>}
     *
     * @param jsonObject json object
     * @return {@code ResponseObject} parsed from {@code JSONObject}
     */
    private static Map<String, Object> parseForcesResponseInternal(JSONObject jsonObject) {
        Map<String, Object> forces = new HashMap<>();
        forces.put(FORCES_ID, jsonObject.getString(FORCES_ID));
        forces.put(FORCES_NAME, jsonObject.getString(FORCES_NAME));
        return forces;
    }


}
