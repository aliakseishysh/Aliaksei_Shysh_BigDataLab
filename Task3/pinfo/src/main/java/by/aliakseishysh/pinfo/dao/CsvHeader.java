package by.aliakseishysh.pinfo.dao;

import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_CATEGORY;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_CONTEXT;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_ID;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_LOCATION_SUBTYPE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_LOCATION_TYPE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_MONTH;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.CRIMES_PERSISTENT_ID;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.LOCATIONS_LATITUDE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.LOCATIONS_LONGITUDE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.OUTCOMES_CATEGORY;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.OUTCOMES_DATE;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.STREETS_ID;
import static by.aliakseishysh.pinfo.dao.DatabaseColumn.STREETS_NAME;


public class CsvHeader {

    public static final String[] ALL_CRIME_HEADERS = new String[] {
            CRIMES_CATEGORY, CRIMES_PERSISTENT_ID, CRIMES_MONTH,
            LOCATIONS_LATITUDE, STREETS_ID, STREETS_NAME,
            LOCATIONS_LONGITUDE, CRIMES_CONTEXT, CRIMES_ID,
            CRIMES_LOCATION_TYPE, CRIMES_LOCATION_TYPE, CRIMES_LOCATION_SUBTYPE,
            OUTCOMES_CATEGORY, OUTCOMES_DATE
    };

}
