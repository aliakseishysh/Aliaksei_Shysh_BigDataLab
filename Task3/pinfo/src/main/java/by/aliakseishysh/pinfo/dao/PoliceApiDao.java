package by.aliakseishysh.pinfo.dao;

import java.util.Map;

public interface PoliceApiDao {

    /**
     * Adds response object from all-crime api to database.
     *
     * @param crime response object parsed from all-crime api
     * @return true if successfully added, false otherwise
     */
    boolean add(Map<String, Object> crime);

    /**
     * Clears data in dao if needed.
     */
    void clear();

}
