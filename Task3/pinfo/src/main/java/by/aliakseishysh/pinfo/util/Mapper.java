package by.aliakseishysh.pinfo.util;

import java.sql.Date;
import java.util.Map;

import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_CATEGORY;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_CONTEXT;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_ID;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_LOCATION_SUBTYPE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_LOCATION_TYPE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_MONTH;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_PERSISTENT_ID;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.LOCATIONS;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.LOCATIONS_LATITUDE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.LOCATIONS_LONGITUDE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.OUTCOMES;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.OUTCOMES_CATEGORY;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.OUTCOMES_DATE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.STREETS;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.STREETS_ID;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.STREETS_NAME;

public class Mapper {

    public static String[] allCrimeResponseMapToStringArray(Map<String, Object> objectMap) {
        String category = (String) objectMap.get(CRIMES_CATEGORY);
        String persistentId = (String) objectMap.get(CRIMES_PERSISTENT_ID);
        Date month = (Date) objectMap.get(CRIMES_MONTH);

        Map<String, Object> location = (Map<String, Object>) objectMap.get(LOCATIONS);
        Map<String, Object> street = (Map<String, Object>) location.get(STREETS);

        String latitude = (String) location.get(LOCATIONS_LATITUDE);
        Long streetId = (Long) street.get(STREETS_ID);
        String name = (String) street.get(STREETS_NAME);
        String longitude = (String) location.get(LOCATIONS_LONGITUDE);

        String context = (String) objectMap.get(CRIMES_CONTEXT);
        Long id = (Long) objectMap.get(CRIMES_ID);
        String locationType = (String) objectMap.get(CRIMES_LOCATION_TYPE);
        String locationSubtype = (String) objectMap.get(CRIMES_LOCATION_SUBTYPE);

        Map<String, Object> outcome = (Map<String, Object>) objectMap.get(OUTCOMES);
        String categoryStatus = outcome != null ? (String) outcome.get(OUTCOMES_CATEGORY) : null;
        Date date = outcome != null ? (Date) outcome.get(OUTCOMES_DATE) : null;

        return new String[] {
                category, persistentId, month.toString(),
                latitude, streetId.toString(), name,
                longitude, context, id.toString(),
                locationType, locationSubtype, categoryStatus,
                date != null ? date.toString() : null
        };
    }

}
