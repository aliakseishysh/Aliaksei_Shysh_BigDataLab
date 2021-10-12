package by.aliakseishysh.pinfo.database;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;

public class FluentConnector {

    private static final FluentJdbc fluentJdbc;

    static {
        fluentJdbc = new FluentJdbcBuilder()
                .connectionProvider(HcpSource.getSource())
                .build();
        // TODO handle exceptions
    }

    public static FluentJdbc getConnector() {
        return  fluentJdbc;
    }

}
