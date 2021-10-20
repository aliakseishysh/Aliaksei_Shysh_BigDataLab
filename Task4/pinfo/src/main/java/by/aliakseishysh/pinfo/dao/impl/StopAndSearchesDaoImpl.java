package by.aliakseishysh.pinfo.dao.impl;

import by.aliakseishysh.pinfo.dao.DatabaseColumn;
import by.aliakseishysh.pinfo.dao.FluentConnector;
import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StopAndSearchesDaoImpl implements PoliceApiDao {

    private static final PoliceApiDao instance = new StopAndSearchesDaoImpl();
    private static final String INSERT_SAS = "INSERT INTO stopAndSearches(type, involved_person, datetime, " +
            "operation, operation_name, location, gender, age_range, self_defined_ethnicity, " +
            "officer_defined_ethnicity, legislation, object_of_search, outcome, outcome_object, " +
            "outcome_linked_to_object_of_search, removal_of_more_than_outer_clothing) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_UNIQUE_SAS_ID = "SELECT street_id FROM streets WHERE id = ?";
    private static final String INSERT_OUTCOME_OBJECT = "INSERT INTO outcome_objects(id, name) VALUES(?, ?)";
    private static final String SELECT_UNIQUE_OUTCOME_OBJECT = "SELECT outcome_object_id FROM outcome_objects WHERE id = ?";


    private StopAndSearchesDaoImpl() {
    }

    public static PoliceApiDao getInstance() {
        return instance;
    }

    /**
     * Adds {@code allCrimeResponseObject} to database (queries runs as transaction)
     *
     * @param sas response object parsed from stop-and-searches api
     * @return true if crime added, false otherwise
     */
    @Override
    public boolean add(Map<String, Object> sas) {
        Query query = FluentConnector.getConnector().query();
        return query.transaction().in(() -> {
            Map<String, Object> location = (Map<String, Object>) sas.get(DatabaseColumn.LOCATIONS);
            Map<String, Object> outcomeObject = (Map<String, Object>) sas.get(DatabaseColumn.OUTCOME_OBJECTS);
            Map<String, Object> street = null;
            Long streetId = null;
            Long locationId = null;
            if (location != null && location != JSONObject.NULL) {
                street = (Map<String, Object>) location.get(DatabaseColumn.STREETS);
                streetId = findStreetId(query, street);
                streetId = streetId == -1 ?
                        addNewStreet(query, (Map<String, Object>) location.get(DatabaseColumn.STREETS)) : streetId;
                locationId = findLocationId(query, location, streetId);
                locationId = locationId == -1 ? addNewLocation(query, location, streetId) : locationId;
            }
            Long outcomeObjectId = null;
            if (outcomeObject != null && outcomeObject != JSONObject.NULL) {
                outcomeObjectId = findOutcomeObjectId(query, outcomeObject);
                outcomeObjectId = outcomeObjectId == -1 ? addNewOutcomeObject(query, outcomeObject) : outcomeObjectId;
            }

            Long sasId = findSasId(query, sas);
            sasId = sasId == -1 ? addNewSas(query, sas, locationId, outcomeObjectId) : sasId;
            return sasId != -1;
        });
    }

    @Override
    public void clear() {
    }

    /**
     * Internal method for adding new street to database
     *
     * @param query      performs operation on query
     * @param sas        map representing stop and search
     * @param locationId id of location
     * @return generated id
     */
    private long addNewSas(Query query, Map<String, Object> sas, Long locationId, Long outcomeObjectId) {
        List<List<?>> queryParameters = new ArrayList<>();
        List<Object> listParameters = new ArrayList<>();
        listParameters.add(sas.get(DatabaseColumn.SAS_TYPE));
        listParameters.add(sas.get(DatabaseColumn.SAS_INVOLVED_PERSON));
        listParameters.add(sas.get(DatabaseColumn.SAS_DATETIME));
        listParameters.add(sas.get(DatabaseColumn.SAS_OPERATION));
        listParameters.add(sas.get(DatabaseColumn.SAS_OPERATION_NAME));
        listParameters.add(locationId);
        listParameters.add(sas.get(DatabaseColumn.SAS_GENDER));
        listParameters.add(sas.get(DatabaseColumn.SAS_AGE_RANGE));
        listParameters.add(sas.get(DatabaseColumn.SAS_SELF_DEFINED_ETHNICITY));
        listParameters.add(sas.get(DatabaseColumn.SAS_OFFICER_DEFINED_ETHNICITY));
        listParameters.add(sas.get(DatabaseColumn.SAS_LEGISLATION));
        listParameters.add(sas.get(DatabaseColumn.SAS_OBJECT_OF_SEARCH));

        listParameters.add(sas.get(DatabaseColumn.SAS_OUTCOME));
        listParameters.add(outcomeObjectId);
        listParameters.add(sas.get(DatabaseColumn.SAS_OUTCOME_LINKED_TO_OBJECT_OF_SEARCH));
        listParameters.add(sas.get(DatabaseColumn.SAS_REMOVAL_OF_MORE_THAN_OUTER_CLOTHING));

        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch(INSERT_SAS)
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).firstKey().orElse(-1L);
    }


    private long addNewOutcomeObject(Query query, Map<String, Object> outcome) {
        List<List<?>> queryParameters = new ArrayList<>();
        List<Object> listParameters = new ArrayList<>();
        listParameters.add(objectOrEmpty(outcome.get(DatabaseColumn.OUTCOME_OBJECTS_ID)));
        listParameters.add(objectOrEmpty(outcome.get(DatabaseColumn.OUTCOME_OBJECTS_NAME)));
        queryParameters.add(listParameters);
        List<UpdateResultGenKeys<Long>> result = query
                .batch(INSERT_OUTCOME_OBJECT)
                .params(queryParameters.iterator())
                .runFetchGenKeys(Mappers.singleLong());
        return result.get(0).firstKey().orElse(-1L);
    }


    private long findSasId(Query query, Map<String, Object> sas) {
        return query.select(SELECT_UNIQUE_SAS_ID)
                .params(sas.get(DatabaseColumn.SAS_SAS_ID))
                .firstResult(Mappers.singleLong()).orElse(-1L);
    }

    private long findOutcomeObjectId(Query query, Map<String, Object> outcome_object) {
        return query.select(SELECT_UNIQUE_OUTCOME_OBJECT)
                .params(outcome_object.get(DatabaseColumn.OUTCOME_OBJECTS_ID))
                .firstResult(Mappers.singleLong()).orElse(-1L);
    }


}
