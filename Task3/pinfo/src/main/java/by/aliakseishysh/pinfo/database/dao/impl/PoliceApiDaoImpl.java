package by.aliakseishysh.pinfo.database.dao.impl;

import by.aliakseishysh.pinfo.database.FluentConnector;
import by.aliakseishysh.pinfo.database.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.entity.AllCrimeResponseObject;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;

import java.util.ArrayList;
import java.util.List;

public class PoliceApiDaoImpl implements PoliceApiDao {

    private static final PoliceApiDao instance = new PoliceApiDaoImpl();

    public static PoliceApiDao getInstance() {
        return instance;
    }

    private PoliceApiDaoImpl() {
    }

    /**
     * Adds {@code allCrimeResponseObject} to database (queries runs as transaction)
     *
     * @param allCrimeResponseObject response object parsed from all-crime api
     * @return true if crime added, false otherwise
     */
    @Override
    public boolean addNewAllCrimeResponseObject(AllCrimeResponseObject allCrimeResponseObject) {
        FluentJdbc connector = FluentConnector.getConnector();
        Query query = connector.query();
        return query.transaction().in(() -> {
            long locationId = addNewLocation(query, allCrimeResponseObject.getLocation());
            long outcomeId = addNewOutcome(query, allCrimeResponseObject.getOutcomeStatus());
            return addNewCrime(query, allCrimeResponseObject, locationId, outcomeId) != -1;
        });
    }

    /**
     * Adds new crime to database
     *
     * @param query                  performs operation on query
     * @param allCrimeResponseObject response object parsed from all-crime api
     * @param locationId             location id acquired from {@link #addNewLocation} method
     * @param outcomeId              outcome id acquired from {@link #addNewOutcome} method
     * @return generated id
     */
    private long addNewCrime(Query query, AllCrimeResponseObject allCrimeResponseObject, long locationId, long outcomeId) {
        String category = allCrimeResponseObject.getCategory();
        String persistentId = allCrimeResponseObject.getPersistentId();
        String month = allCrimeResponseObject.getMonth();
        String context = allCrimeResponseObject.getContext();
        long id = allCrimeResponseObject.getId();
        String locationType = allCrimeResponseObject.getLocationType();
        String locationSubtype = allCrimeResponseObject.getLocationSubtype();

        List<List<?>> queryParameters = new ArrayList<>(2);
        List<Object> listParameters = new ArrayList<>(12);
        listParameters.add(category);
        listParameters.add(persistentId);
        listParameters.add(month);
        listParameters.add(locationId);
        listParameters.add(context);
        listParameters.add(id);
        listParameters.add(locationType);
        listParameters.add(locationSubtype);
        listParameters.add(outcomeId);
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch("INSERT INTO crimes(category, persistent_id, month, location, context, id, location_type," +
                        " location_subtype, outcome_status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).firstKey().orElse(-1L);
    }


    /**
     * Internal method for adding new street to database
     *
     * @param query  performs operation on query
     * @param street {@code Street} object
     * @return generated id
     */
    private long addNewStreet(Query query, AllCrimeResponseObject.Location.Street street) {
        long id = street.getId();
        String name = street.getName();
        List<List<?>> queryParameters = new ArrayList<>(2);
        List<Object> listParameters = new ArrayList<>(3);
        listParameters.add(id);
        listParameters.add(name);
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch("INSERT INTO streets(id, name) VALUES(?, ?)")
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).generatedKeys().get(0);
    }

    /**
     * Internal method for adding new location to database
     *
     * @param query    performs operation on query
     * @param location {@code Location} object
     * @return generated id
     */
    private long addNewLocation(Query query, AllCrimeResponseObject.Location location) {
        String latitude = location.getLatitude();
        String longitude = location.getLongtitude();
        List<List<?>> queryParameters = new ArrayList<>(2);
        List<Object> listParameters = new ArrayList<>(4);
        listParameters.add(latitude);
        listParameters.add(addNewStreet(query, location.getStreet()));
        listParameters.add(longitude);
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch("INSERT INTO locations(latitude, street, longitude) VALUES(?, ?, ?)")
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).generatedKeys().get(0);
    }

    /**
     * Internal method for adding new outcome to database
     *
     * @param query         performs operation on query
     * @param outcomeStatus {@code OutcomeStatus} object
     * @return generated id
     */
    private long addNewOutcome(Query query, AllCrimeResponseObject.OutcomeStatus outcomeStatus) {
        String category = outcomeStatus != null ? outcomeStatus.getCategory() : null;
        String date = outcomeStatus != null ? outcomeStatus.getDate() : null;
        List<List<?>> queryParameters = new ArrayList<>(2);
        List<Object> listParameters = new ArrayList<>(3);
        listParameters.add(category);
        listParameters.add(date);
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch("INSERT INTO outcomes(category, date) VALUES(?, ?)")
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).generatedKeys().get(0);
    }


}
