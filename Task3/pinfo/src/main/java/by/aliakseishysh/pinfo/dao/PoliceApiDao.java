package by.aliakseishysh.pinfo.dao;

import java.util.List;
import java.util.Map;

public interface PoliceApiDao {

    /**
     * Adds response object from all-crime api to database
     *
     * @param allCrimeResponseMap response object parsed from all-crime api
     * @return true if successfully added, false otherwise
     */
    boolean addNewAllCrimeResponseObject(Map<String, Object> allCrimeResponseMap);
    void clear();
    void init(String... args);

}
