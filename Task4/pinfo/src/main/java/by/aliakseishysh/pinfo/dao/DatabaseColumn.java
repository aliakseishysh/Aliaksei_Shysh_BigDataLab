package by.aliakseishysh.pinfo.dao;

/**
 * Represents database column names
 */
public class DatabaseColumn {

    // streets
    public static final String STREETS = "streets";
    public static final String STREETS_STREET_ID = "street_id";
    public static final String STREETS_ID = "id";
    public static final String STREETS_NAME = "name";

    // locations
    public static final String LOCATIONS = "locations";
    public static final String LOCATIONS_LOCATION_ID = "location_id";
    public static final String LOCATIONS_LATITUDE = "latitude";
    public static final String LOCATIONS_STREET = "street";
    public static final String LOCATIONS_LONGITUDE = "longitude";
    // outcomes
    public static final String OUTCOMES = "outcomes";
    public static final String OUTCOMES_STATUS_ID = "status_id";
    public static final String OUTCOMES_CATEGORY = "category";
    public static final String OUTCOMES_DATE = "date";
    // crimes
    public static final String CRIMES = "crimes";
    public static final String CRIMES_CRIME_ID = "crime_id";
    public static final String CRIMES_CATEGORY = "category";
    public static final String CRIMES_PERSISTENT_ID = "persistent_id";
    public static final String CRIMES_MONTH = "month";
    public static final String CRIMES_LOCATION = "location";
    public static final String CRIMES_CONTEXT = "context";
    public static final String CRIMES_ID = "id";
    public static final String CRIMES_LOCATION_TYPE = "location_type";
    public static final String CRIMES_LOCATION_SUBTYPE = "location_subtype";
    public static final String CRIMES_OUTCOME_STATUS = "outcome_status";
    // stopAndSearches
    public static final String SAS = "stopAndSearches";
    public static final String SAS_SAS_ID = "sas_id";
    public static final String SAS_TYPE = "type";
    public static final String SAS_INVOLVED_PERSON = "involved_person";
    public static final String SAS_DATETIME = "datetime";
    public static final String SAS_OPERATION = "operation";
    public static final String SAS_OPERATION_NAME = "operation_name";
    public static final String SAS_LOCATION = "location";
    public static final String SAS_GENDER = "gender";
    public static final String SAS_AGE_RANGE = "age_range";
    public static final String SAS_SELF_DEFINED_ETHNICITY = "self_defined_ethnicity";
    public static final String SAS_OFFICER_DEFINED_ETHNICITY = "officer_defined_ethnicity";
    public static final String SAS_LEGISLATION = "legislation";
    public static final String SAS_OBJECT_OF_SEARCH = "object_of_search";
    public static final String SAS_OUTCOME = "outcome";
    public static final String SAS_OUTCOME_LINKED_TO_OBJECT_OF_SEARCH = "outcome_linked_to_object_of_search";
    public static final String SAS_REMOVAL_OF_MORE_THAN_OUTER_CLOTHING = "removal_of_more_than_outer_clothing";
    // outcome_objects
    public static final String OUTCOME_OBJECTS = "outcome_objects";
    public static final String OUTCOME_OBJECTS_OUTCOME_OBJECT_ID_ = "outcome_object_id";
    public static final String OUTCOME_OBJECTS_ID = "id";
    public static final String OUTCOME_OBJECTS_NAME = "name";

    // potential table forces
    public static final String FORCES_ID = "id";
    public static final String FORCES_NAME = "name";



}
