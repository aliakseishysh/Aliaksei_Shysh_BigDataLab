package by.aliakseishysh.pinfo.dao;

import static by.aliakseishysh.pinfo.dao.DatabaseColumn.*;


public class CsvHeader {

    public static final String[] ALL_CRIME_HEADERS = new String[] {
            CRIMES_CATEGORY, CRIMES_PERSISTENT_ID, CRIMES_MONTH,
            LOCATIONS_LATITUDE, STREETS_ID, STREETS_NAME,
            LOCATIONS_LONGITUDE, CRIMES_CONTEXT, CRIMES_ID,
            CRIMES_LOCATION_TYPE, CRIMES_LOCATION_TYPE, CRIMES_LOCATION_SUBTYPE,
            OUTCOMES_CATEGORY, OUTCOMES_DATE
    };

}
