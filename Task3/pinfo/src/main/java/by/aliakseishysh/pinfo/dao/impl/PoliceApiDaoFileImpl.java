package by.aliakseishysh.pinfo.dao.impl;

import by.aliakseishysh.pinfo.dao.CsvHeader;
import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.util.CsvWriter;
import by.aliakseishysh.pinfo.util.Mapper;

import java.util.Map;

public class PoliceApiDaoFileImpl  implements PoliceApiDao {


    private static final PoliceApiDao instance = new PoliceApiDaoFileImpl();
    private static boolean isHeaderWritten = false;
    private static String filePath = null;

    private PoliceApiDaoFileImpl() {
    }

    public static PoliceApiDao getInstance() { return instance; }


    /**
     * Adds response object from all-crime api to database
     *
     * @param allCrimeResponseMap response object parsed from all-crime api
     * @return true if successfully added, false otherwise
     */
    @Override
    public boolean addNewAllCrimeResponseObject(Map<String, Object> allCrimeResponseMap) {
        String[] data;
        if (isHeaderWritten == false) {
            data = CsvHeader.ALL_CRIME_HEADERS;
            isHeaderWritten = true;
        } else {
            data = Mapper.allCrimeResponseMapToStringArray(allCrimeResponseMap);
        }
        CsvWriter.writeCsvAllCrime(filePath, data);
        return true; // TODO handle
    }

    @Override
    public void clear() {
        isHeaderWritten = false;
        filePath = null;
    }

    @Override
    public void init(String... args) {
        filePath = args[0];
    }

}
