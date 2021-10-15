package by.aliakseishysh.pinfo.util;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class NameValuePairBuilder {
    private final List<NameValuePair> pairs;

    private NameValuePairBuilder() {
        pairs = new ArrayList<>();
    }

    public static NameValuePairBuilder newBuilder() {
        return new NameValuePairBuilder();
    }

    public NameValuePairBuilder addPair(String name, String value) {
        pairs.add(new NameValuePair() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getValue() {
                return value;
            }
        });
        return this;
    }

    public List<NameValuePair> build() {
        return this.pairs;
    }
}