package by.aliakseishysh.pinfo.database.dao;

import by.aliakseishysh.pinfo.entity.AllCrimeResponseObject;

public interface PoliceApiDao {

    /**
     * Adds response object from all-crime api to database
     *
     * @param allCrimeResponseObject response object parsed from all-crime api
     * @return true if successfully added, false otherwise
     */
    boolean addNewAllCrimeResponseObject(AllCrimeResponseObject allCrimeResponseObject);

}
