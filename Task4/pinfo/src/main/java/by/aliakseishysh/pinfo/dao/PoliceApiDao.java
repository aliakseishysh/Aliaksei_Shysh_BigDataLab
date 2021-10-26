package by.aliakseishysh.pinfo.dao;

import by.aliakseishysh.pinfo.exception.FileException;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;

import java.util.ArrayList;
import java.util.List;
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
    void clear() throws FileException;

    /**
     * Internal method for adding new street to database
     *
     * @param query  performs operation on query
     * @param street map representing street
     * @return generated id
     */
    default long addNewStreet(Query query, Map<String, Object> street) {
        final String INSERT_STREET = "INSERT INTO streets(id, name) VALUES(?, ?)";
        List<List<?>> queryParameters = new ArrayList<>();
        List<Object> listParameters = new ArrayList<>();
        listParameters.add(street.get(DatabaseColumn.STREETS_ID));
        listParameters.add(street.get(DatabaseColumn.STREETS_NAME));
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch(INSERT_STREET)
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
    default long addNewLocation(Query query, Map<String, Object> location, long streetId) {
        final String INSERT_LOCATION = "INSERT INTO locations(latitude, street, longitude) VALUES(?, ?, ?)";
        List<List<?>> queryParameters = new ArrayList<>();
        List<Object> listParameters = new ArrayList<>();
        listParameters.add(location.get(DatabaseColumn.LOCATIONS_LATITUDE));
        listParameters.add(streetId);
        listParameters.add(location.get(DatabaseColumn.LOCATIONS_LONGITUDE));
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch(INSERT_LOCATION)
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).firstKey().orElse(-1L);
    }

    /**
     * Method for finding street id.
     *
     * @param query  performs operation on query
     * @param street map representing street
     * @return street id or -1 otherwise
     */
    default long findStreetId(Query query, Map<String, Object> street) {
        final String SELECT_UNIQUE_STREET_ID = "SELECT street_id FROM streets WHERE id = ? OR name = ?";
        return query.select(SELECT_UNIQUE_STREET_ID)
                .params(street.get(DatabaseColumn.STREETS_ID), street.get(DatabaseColumn.STREETS_NAME))
                .firstResult(Mappers.singleLong()).orElse(-1L);
    }

    /**
     * Method for finding location id.
     *
     * @param query    performs operation on query
     * @param location map representing location
     * @param streetId street id
     * @return street id or -1 otherwise
     */
    default long findLocationId(Query query, Map<String, Object> location, long streetId) {
        final String SELECT_UNIQUE_LOCATION_ID = "SELECT location_id FROM locations WHERE latitude = ? " +
                "AND street = ? AND longitude = ?";
        return query.select(SELECT_UNIQUE_LOCATION_ID)
                .params(location.get(DatabaseColumn.LOCATIONS_LATITUDE), streetId,
                        location.get(DatabaseColumn.LOCATIONS_LONGITUDE))
                .firstResult(Mappers.singleLong()).orElse(-1L);
    }
    
}
