package by.aliakseishysh.pinfo.dao.impl;

import by.aliakseishysh.pinfo.dao.DatabaseColumn;
import by.aliakseishysh.pinfo.dao.FluentConnector;
import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllCrimesDaoImpl implements PoliceApiDao {

    private static final PoliceApiDao instance = new AllCrimesDaoImpl();
    private static final String INSERT_CRIME = "INSERT INTO crimes(category, persistent_id, month, location, context," +
            " id, location_type, location_subtype, outcome_status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_OUTCOME = "INSERT INTO outcomes(category, date) VALUES(?, ?)";
    private static final String SELECT_UNIQUE_CRIME_ID = "SELECT crime_id FROM crimes WHERE id = ? OR persistent_id = ?";

    private AllCrimesDaoImpl() {
    }

    public static PoliceApiDao getInstance() {
        return instance;
    }

    /**
     * Adds {@code allCrimeResponseObject} to database (queries runs as transaction)
     *
     * @param crime response object parsed from all-crime api
     * @return true if crime added, false otherwise
     */
    @Override
    public boolean add(Map<String, Object> crime) {
        Query query = FluentConnector.getConnector().query();
        return query.transaction().in(() -> {
            Map<String, Object> location = (Map<String, Object>) crime.get(DatabaseColumn.LOCATIONS);
            Map<String, Object> outcome = (Map<String, Object>) crime.get(DatabaseColumn.OUTCOMES);
            Map<String, Object> street = (Map<String, Object>) location.get(DatabaseColumn.STREETS);

            long streetId = findStreetId(query, street);
            streetId = streetId == -1 ?
                    addNewStreet(query, (Map<String, Object>) location.get(DatabaseColumn.STREETS)) : streetId;

            long locationId = findLocationId(query, location, streetId);
            locationId = locationId == -1 ? addNewLocation(query, location, streetId) : locationId;

            Long outcomeId = outcome != null ? addNewOutcome(query, outcome) : null;

            long crimeId = findCrimeId(query, crime);
            crimeId = crimeId == -1 ? addNewCrime(query, crime, locationId, outcomeId) : crimeId;

            return crimeId != -1;
        });
    }

    @Override
    public void clear() {
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
        List<List<?>> queryParameters = new ArrayList<>();
        List<Object> listParameters = new ArrayList<>();
        listParameters.add(objectOrEmpty(allCrimeResponseObject.get(DatabaseColumn.CRIMES_CATEGORY)));
        listParameters.add(objectOrEmpty(allCrimeResponseObject.get(DatabaseColumn.CRIMES_PERSISTENT_ID)));
        listParameters.add(objectOrEmpty(allCrimeResponseObject.get(DatabaseColumn.CRIMES_MONTH)));
        listParameters.add(objectOrEmpty(locationId));
        listParameters.add(objectOrEmpty(allCrimeResponseObject.get(DatabaseColumn.CRIMES_CONTEXT)));
        listParameters.add(objectOrEmpty(allCrimeResponseObject.get(DatabaseColumn.CRIMES_ID)));
        listParameters.add(objectOrEmpty(allCrimeResponseObject.get(DatabaseColumn.CRIMES_LOCATION_TYPE)));
        listParameters.add(objectOrEmpty(allCrimeResponseObject.get(DatabaseColumn.CRIMES_LOCATION_SUBTYPE)));
        listParameters.add(objectOrEmpty(outcomeId));
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch(INSERT_CRIME)
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
        List<List<?>> queryParameters = new ArrayList<>();
        List<Object> listParameters = new ArrayList<>();
        listParameters.add(outcomeStatus != null ? outcomeStatus.get(DatabaseColumn.OUTCOMES_CATEGORY) : null);
        listParameters.add(outcomeStatus != null ? outcomeStatus.get(DatabaseColumn.OUTCOMES_DATE) : null);
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch(INSERT_OUTCOME)
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).generatedKeys().get(0);
    }

    /**
     * Method for finding crime id.
     *
     * @param query performs operation on query
     * @param crime map representing crime
     * @return crime id or -1 otherwise
     */
    private long findCrimeId(Query query, Map<String, Object> crime) {
        return query.select(SELECT_UNIQUE_CRIME_ID)
                .params(crime.get(DatabaseColumn.CRIMES_ID), crime.get(DatabaseColumn.CRIMES_PERSISTENT_ID))
                .firstResult(Mappers.singleLong()).orElse(-1L);
    }


}
