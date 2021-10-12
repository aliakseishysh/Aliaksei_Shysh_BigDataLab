package by.aliakseishysh.pinfo.util;

import by.aliakseishysh.pinfo.entity.ResponseObject;
import static by.aliakseishysh.pinfo.database.DatabaseColumn.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ResponseParser {

    public static List<ResponseObject> parse(String jsonString) {
        List<ResponseObject> resultList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        Iterator<Object> iterator = jsonArray.iterator();
        iterator.forEachRemaining((el) -> resultList.add(parseObject((JSONObject) el)));
        System.out.println(resultList.get(0).toString());
        return resultList;
    }

    private static ResponseObject parseObject(JSONObject object) {
        JSONObject location = object.getJSONObject(CRIMES_LOCATION);
        JSONObject street = location.getJSONObject(LOCATIONS_STREET);
        JSONObject outcomeStatus = object.optJSONObject(CRIMES_OUTCOME_STATUS);
        return ResponseObject.newBuilder()
                .setCategory(object.getString(CRIMES_CATEGORY))
                .setPersistentId(object.getString(CRIMES_PERSISTENT_ID))
                .setMonth(object.getString(CRIMES_MONTH))
                .setLocationLatitude(location.getString(LOCATIONS_LATITUDE))
                .setLocationStreetId(street.getLong(STREETS_ID))
                .setLocationStreetName(street.getString(STREETS_NAME))
                .setLocationLongtitude(location.getString(LOCATIONS_LONGITUDE))
                .setContext(object.getString(CRIMES_CONTEXT))
                .setId(object.getLong(CRIMES_ID))
                .setLocationType(object.getString(CRIMES_LOCATION_TYPE))
                .setLocationSubtype(object.getString(CRIMES_LOCATION_SUBTYPE))
                .setOutcomeStatusCategory(outcomeStatus != null ? outcomeStatus.getString(OUTCOMES_CATEGORY) : null)
                .setOutcomeStatusDate(outcomeStatus != null ? outcomeStatus.getString(OUTCOMES_DATE) : null)
                .build();
    }



}
