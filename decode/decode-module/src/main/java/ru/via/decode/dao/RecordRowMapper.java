package ru.via.decode.dao;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RecordRowMapper implements RowMapper<DecodeRecord> {

    @Override
    public DecodeRecord mapRow(ResultSet rs, int i) throws SQLException {
        DecodeRecord result = new DecodeRecord();
        result.setSourceValue(rs.getString("V_SOURCE_VALUE"));
        result.setTargetValue(rs.getString("V_TARGET_VALUE"));
        return result;
    }
}