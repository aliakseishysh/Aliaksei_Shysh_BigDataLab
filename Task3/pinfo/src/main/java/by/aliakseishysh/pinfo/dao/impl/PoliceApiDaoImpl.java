package by.aliakseishysh.pinfo.dao.impl;

import by.aliakseishysh.pinfo.dao.DatabaseColumn;
import by.aliakseishysh.pinfo.dao.FluentConnector;
import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PoliceApiDaoImpl implements PoliceApiDao {

    private static final PoliceApiDao instance = new PoliceApiDaoImpl();

    private PoliceApiDaoImpl() {
    }

    public static PoliceApiDao getInstance() {
        return instance;
    }

    /**
     * Adds {@code allCrimeResponseObject} to database (queries runs as transaction)
     *
     * @param allCrimeResponseMap response object parsed from all-crime api
     * @return true if crime added, false otherwise
     */
    @Override
    public boolean addNewAllCrimeResponseObject(Map<String, Object> allCrimeResponseMap) {
        FluentJdbc connector = FluentConnector.getConnector();
        Query query = connector.query();

        return query.transaction().in(() -> {
            Map<String, Object> location = (Map<String, Object>) allCrimeResponseMap.get(DatabaseColumn.LOCATIONS);
            Map<String, Object> outcome = (Map<String, Object>) allCrimeResponseMap.get(DatabaseColumn.OUTCOMES);
            Map<String, Object> street = (Map<String, Object>) location.get(DatabaseColumn.STREETS);
            long streetId = findStreetId(query, street);
            long locationId;
            if (streetId != -1L) {
                locationId = findLocationId(query, location, streetId);
                if (locationId == -1L) {
                    locationId = addNewLocation(query, location, streetId);
                }
            } else {
                locationId = addNewLocation(query, location, streetId);
            }
            Long outcomeId = outcome != null ? addNewOutcome(query, outcome) : null;
            return addNewCrime(query, allCrimeResponseMap, locationId, outcomeId) != -1; // -1: can't add
        });
    }

    @Override
    public void clear() {
    }

    @Override
    public void init(String... args) {
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
    private long addNewCrime(Query query, Map<String, Object> allCrimeResponseObject, long locationId, Long outcomeId) {
        String category = (String) allCrimeResponseObject.get(DatabaseColumn.CRIMES_CATEGORY);
        String persistentId = (String) allCrimeResponseObject.get(DatabaseColumn.CRIMES_PERSISTENT_ID);
        Date month = (Date) allCrimeResponseObject.get(DatabaseColumn.CRIMES_MONTH);
        String context = (String) allCrimeResponseObject.get(DatabaseColumn.CRIMES_CONTEXT);
        long id = (long) allCrimeResponseObject.get(DatabaseColumn.CRIMES_ID);
        String locationType = (String) allCrimeResponseObject.get(DatabaseColumn.CRIMES_LOCATION_TYPE);
        String locationSubtype = (String) allCrimeResponseObject.get(DatabaseColumn.CRIMES_LOCATION_SUBTYPE);

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
     * @param street map representing street
     * @return generated id
     */
    private long addNewStreet(Query query, Map<String, Object> street) {
        long id = (long) street.get(DatabaseColumn.STREETS_ID);
        String name = (String) street.get(DatabaseColumn.STREETS_NAME);
        List<List<?>> queryParameters = new ArrayList<>(2);
        List<Object> listParameters = new ArrayList<>(3);
        listParameters.add(id);
        listParameters.add(name);
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch("INSERT INTO streets(id, name) VALUES(?, ?)")
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).firstKey().orElse(-1L);
    }

    /**
     * Internal method for adding new location to database
     *
     * @param query    performs operation on query
     * @param location map representing location
     * @return generated id
     */
    private long addNewLocation(Query query, Map<String, Object> location, long streetId) {
        String latitude = (String) location.get(DatabaseColumn.LOCATIONS_LATITUDE);
        String longitude = (String) location.get(DatabaseColumn.LOCATIONS_LONGITUDE);
        List<List<?>> queryParameters = new ArrayList<>(2);
        List<Object> listParameters = new ArrayList<>(4);
        listParameters.add(latitude);
        if (streetId != -1L) {
            listParameters.add(streetId);
        } else {
            listParameters.add(addNewStreet(query, (Map<String, Object>) location.get(DatabaseColumn.STREETS)));
        }
        listParameters.add(longitude);
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch("INSERT INTO locations(latitude, street, longitude) VALUES(?, ?, ?)")
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).firstKey().orElse(-1L);
    }

    /**
     * Internal method for adding new outcome to database
     *
     * @param query         performs operation on query
     * @param outcomeStatus map representing outcome status
     * @return generated id
     */
    private long addNewOutcome(Query query, Map<String, Object> outcomeStatus) {
        String category = outcomeStatus != null ? (String) outcomeStatus.get(DatabaseColumn.OUTCOMES_CATEGORY) : null;
        Date date = outcomeStatus != null ? (Date) outcomeStatus.get(DatabaseColumn.OUTCOMES_DATE) : null;
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

    /**
     * Method for finding street id.
     *
     * @param query performs operation on query
     * @param street map representing street
     * @return street id or -1 otherwise
     */
    private long findStreetId(Query query, Map<String, Object> street) {
        long id = (long) street.get(DatabaseColumn.STREETS_ID);
        String name = (String) street.get(DatabaseColumn.STREETS_NAME);
        // TODO streets with different id's and same names.....
        return query.select("SELECT street_id FROM streets WHERE id = ? OR name = ?")
                .params(id, name)
                .firstResult(Mappers.singleLong()).orElse(-1L);
    }

    /**
     * Method for finding location id.
     *
     * @param query performs operation on query
     * @param location map representing location
     * @param streetId street id
     * @return street id or -1 otherwise
     */
    private long findLocationId(Query query, Map<String, Object> location, long streetId) {
        String latitude = (String) location.get(DatabaseColumn.LOCATIONS_LATITUDE);
        String longitude = (String) location.get(DatabaseColumn.LOCATIONS_LONGITUDE);
        return query.select("SELECT location_id FROM locations WHERE latitude = ? AND street = ? AND longitude = ?")
                .params(latitude, streetId, longitude)
                .firstResult(Mappers.singleLong()).orElse(-1L);
    }


}
