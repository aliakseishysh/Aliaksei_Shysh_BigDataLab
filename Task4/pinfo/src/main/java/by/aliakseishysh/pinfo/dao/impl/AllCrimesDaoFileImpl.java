package by.aliakseishysh.pinfo.dao.impl;

import by.aliakseishysh.pinfo.dao.CsvHeader;
import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.exception.FileException;
import by.aliakseishysh.pinfo.util.CsvWriter;
import by.aliakseishysh.pinfo.util.Mapper;
import org.codejargon.fluentjdbc.api.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AllCrimesDaoFileImpl implements PoliceApiDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllCrimesDaoFileImpl.class);
    private boolean isHeaderWritten;
    private CsvWriter csvWriter;

    public AllCrimesDaoFileImpl(boolean isHeaderWritten, CsvWriter csvWriter) {
        this.csvWriter = csvWriter;
        this.isHeaderWritten = isHeaderWritten;
    }


    /**
     * Adds response object from all-crime api to database
     *
     * @param crime response object parsed from all-crime api
     * @return true if successfully added, false otherwise
     */
    @Override
    public boolean add(Map<String, Object> crime) {
        try {
            if (!isHeaderWritten) {
                csvWriter.writeLine(CsvHeader.ALL_CRIME_HEADERS);
                isHeaderWritten = true;
            }
            String[] data = Mapper.allCrimeResponseMapToStringArray(crime);
            csvWriter.writeLine(data);
            return true;
        } catch (FileException e) {
            LOGGER.error("Can't write to file. " + e.getCause(), e);
            return false;
        }
    }

    @Override
    public void clear() throws FileException {
        isHeaderWritten = false;
        csvWriter.close();
        csvWriter = null;
    }

    @Override
    public long addNewStreet(Query query, Map<String, Object> street) {
        throw new UnsupportedOperationException("Operation not supported");
    }
    @Override
    public long addNewLocation(Query query, Map<String, Object> location, long streetId) {
        throw new UnsupportedOperationException("Operation not supported");
    }
    @Override
    public long findStreetId(Query query, Map<String, Object> street) {
        throw new UnsupportedOperationException("Operation not supported");
    }
    @Override
    public long findLocationId(Query query, Map<String, Object> location, long streetId) {
        throw new UnsupportedOperationException("Operation not supported");
    }
}
