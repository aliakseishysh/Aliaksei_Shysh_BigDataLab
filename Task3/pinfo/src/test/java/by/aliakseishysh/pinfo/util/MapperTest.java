package by.aliakseishysh.pinfo.util;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.Date;
import java.util.HashMap;
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

public class MapperTest {

    private Map<String, Object> crimes;

    @BeforeTest
    public void setup() {
        crimes = new HashMap<>();
        Map<String, Object> streets = new HashMap<>();
        Map<String, Object> locations = new HashMap<>();
        Map<String, Object> outcomes = new HashMap<>();

        crimes.put(CRIMES_CATEGORY, "cat");
        crimes.put(CRIMES_PERSISTENT_ID, "id");
        crimes.put(CRIMES_MONTH, Date.valueOf("2020-01-01"));
        locations.put(LOCATIONS_LATITUDE, "loc");
        streets.put(STREETS_ID, 1L);
        streets.put(STREETS_NAME, "name");
        locations.put(LOCATIONS_LONGITUDE, "long");
        crimes.put(CRIMES_CONTEXT, "cont");
        crimes.put(CRIMES_ID, 1L);
        crimes.put(CRIMES_LOCATION_TYPE, "lt");
        crimes.put(CRIMES_LOCATION_SUBTYPE, "ls");
        outcomes.put(OUTCOMES_CATEGORY, "oc");
        outcomes.put(OUTCOMES_DATE, Date.valueOf("2020-01-01"));
        crimes.put(LOCATIONS, locations);
        locations.put(STREETS, streets);
        crimes.put(OUTCOMES, outcomes);
    }

    @Test
    public void allCrimeResponseMapToStringArrayTest() {
        String[] expected = new String[] {
                "cat", "id", "2020-01-01",
                "loc", "1", "name",
                "long", "cont", "1",
                "lt", "ls", "oc",
                "2020-01-01"
        };
        String[] actual = Mapper.allCrimeResponseMapToStringArray(crimes);
        Assert.assertEquals(actual, expected);
    }
}
