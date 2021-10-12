package by.aliakseishysh.pinfo.database.dao.impl;

import by.aliakseishysh.pinfo.database.FluentConnector;
import by.aliakseishysh.pinfo.database.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.entity.ResponseObject;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.query.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class PoliceApiDaoImpl implements PoliceApiDao {

    private static final PoliceApiDao instance = new PoliceApiDaoImpl();

    public static PoliceApiDao getInstance() {
        return instance;
    }

    private PoliceApiDaoImpl() {}

    // TODO implement
    @Override
    public boolean addNewResponseObject(ResponseObject responseObject) {
        FluentJdbc connector = FluentConnector.getConnector();
        Query query = connector.query();

        //query.transaction().in(() -> {
            addNewStreet(query, responseObject.getLocation().getStreet());

            return false;
       // });

        //throw new UnsupportedOperationException();
    }

    private long addNewStreet(Query query, ResponseObject.Location.Street street) {
        long id = street.getId();
        String name = street.getName();
        List<List<Object>> result = new ArrayList<>(3);
        List<Object> params = new ArrayList<>(2);
        params.add(id);
        params.add(name);
        result.add(params);

        Iterator<List<?>> iterator = new Iterator<List<?>>() {
            private final Iterator<List<Object>> iter = result.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public List<?> next() {
                return iter.next();
            }
        };
        query.batch("INSERT INTO streets(id, name) VALUES(?, ?)")
                .params(iterator)
                .run();
        return 0;
    }
}
