package by.aliakseishysh.pinfo.dao.impl;

import by.aliakseishysh.pinfo.dao.CsvHeader;
import by.aliakseishysh.pinfo.dao.PoliceApiDao;
import by.aliakseishysh.pinfo.util.CsvWriter;
import by.aliakseishysh.pinfo.util.Mapper;

import java.util.List;
import java.util.Map;

public class PoliceApiDaoFileImpl  implements PoliceApiDao {

    private boolean isHeaderWritten = false;
    private CsvWriter csvWriter;

    public PoliceApiDaoFileImpl(boolean isHeaderWritten, CsvWriter csvWriter) {
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
        if (isHeaderWritten == false) {
            csvWriter.writeLine(CsvHeader.ALL_CRIME_HEADERS);
            isHeaderWritten = true;
        }
        String[] data = Mapper.allCrimeResponseMapToStringArray(crime);
        csvWriter.writeLine(data);
        return true; // TODO handle
    }

    @Override
    public void clear() {
        isHeaderWritten = false;
        csvWriter = null;
    }

}
